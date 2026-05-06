$ErrorActionPreference = "Stop"

$mainBase = "C:\Users\LUIS\IdeaProjects\20261LogisticsInvoice\backend\src\main\java\com\logistica"
$testBase = "C:\Users\LUIS\IdeaProjects\20261LogisticsInvoice\backend\src\test\java\com\logistica"
$allBases = @($mainBase, $testBase)

$modulePrefix = [ordered]@{
    "cierreRuta"  = "CierreRuta"
    "contratos"   = "Contratos"
    "liquidacion" = "Liquidacion"
}
$standardLayers = @("application", "domain", "infrastructure")

# ── 1. Package-path replacement map ───────────────────────────────────────────
$pkgMap = [ordered]@{}
foreach ($mod in $modulePrefix.Keys) {
    foreach ($lyr in $standardLayers) {
        $pkgMap["com.logistica.$mod.$lyr"] = "com.logistica.$lyr.$mod"
    }
}

# ── 2. Class-name replacement map (scanned from actual file stems) ─────────────
$classMap = @{}
foreach ($mod in $modulePrefix.Keys) {
    $prefix = $modulePrefix[$mod]
    foreach ($base in $allBases) {
        $modDir = Join-Path $base $mod
        if (-not (Test-Path $modDir)) { continue }
        Get-ChildItem -Recurse $modDir -Include "*.java" -File | ForEach-Object {
            $stem = $_.BaseName
            if ($stem.StartsWith($prefix) -and $stem.Length -gt $prefix.Length -and -not $classMap.ContainsKey($stem)) {
                $classMap[$stem] = $stem.Substring($prefix.Length)
            }
        }
    }
}
# Sort longest-first to prevent partial replacements
$sortedClasses = $classMap.GetEnumerator() | Sort-Object { $_.Key.Length } -Descending

# ── Helper: apply all substitutions to content ────────────────────────────────
function Transform-Content([string]$txt) {
    foreach ($e in $pkgMap.GetEnumerator()) {
        $txt = $txt.Replace($e.Key, $e.Value)
    }
    foreach ($e in $sortedClasses) {
        $txt = [regex]::Replace($txt, "\b$([regex]::Escape($e.Key))\b", $e.Value)
    }
    return $txt
}

# ── Helper: read/write UTF-8 without BOM ─────────────────────────────────────
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
function Read-Java([string]$path) {
    return [System.IO.File]::ReadAllText($path, $utf8NoBom)
}
function Write-Java([string]$path, [string]$content) {
    $dir = Split-Path $path -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($path, $content, $utf8NoBom)
}

# ── Phase 1: transform & move files from old module directories ───────────────
Write-Host "=== Phase 1: Moving module files to layer structure ===" -ForegroundColor Cyan

$writtenPaths = [System.Collections.Generic.HashSet[string]]::new(
    [System.StringComparer]::OrdinalIgnoreCase)

foreach ($mod in $modulePrefix.Keys) {
    $prefix = $modulePrefix[$mod]
    foreach ($base in $allBases) {

        # Standard layers: module/layer/... -> layer/module/...
        foreach ($lyr in $standardLayers) {
            $oldDir = Join-Path $base "$mod\$lyr"
            if (-not (Test-Path $oldDir)) { continue }

            Get-ChildItem -Recurse $oldDir -Include "*.java" -File | ForEach-Object {
                $oldPath  = $_.FullName
                $rel      = $_.FullName.Substring($oldDir.Length + 1)
                $dirPart  = Split-Path $rel -Parent
                $fileName = Split-Path $rel -Leaf
                $stem     = [IO.Path]::GetFileNameWithoutExtension($fileName)

                # Rename file stem if it carries the module prefix
                $newStem = if ($stem.StartsWith($prefix) -and $stem.Length -gt $prefix.Length) {
                    $stem.Substring($prefix.Length)
                } else { $stem }
                $newFile = "$newStem.java"
                $newRel  = if ($dirPart) { Join-Path $dirPart $newFile } else { $newFile }
                $newPath = Join-Path $base "$lyr\$mod\$newRel"

                Write-Java $newPath (Transform-Content (Read-Java $oldPath))
                [void]$writtenPaths.Add($newPath)
                Write-Host "  [$mod/$lyr] $fileName -> $lyr/$mod/$newFile"
            }
        }

        # Special: module/web/... -> infrastructure/module/web/... (e.g. contratos/web)
        $webDir = Join-Path $base "$mod\web"
        if (Test-Path $webDir) {
            Get-ChildItem -Recurse $webDir -Include "*.java" -File | ForEach-Object {
                $rel     = $_.FullName.Substring($webDir.Length + 1)
                $newPath = Join-Path $base "infrastructure\$mod\web\$rel"
                Write-Java $newPath (Transform-Content (Read-Java $_.FullName))
                [void]$writtenPaths.Add($newPath)
                Write-Host "  [$mod/web] $(Split-Path $rel -Leaf) -> infrastructure/$mod/web/"
            }
        }
    }
}

# ── Phase 2: in-place update of files already in layer dirs ──────────────────
Write-Host "=== Phase 2: Updating imports in existing layer files ===" -ForegroundColor Cyan

foreach ($base in $allBases) {
    # Files inside standard layer dirs
    foreach ($lyr in $standardLayers) {
        $layerDir = Join-Path $base $lyr
        if (-not (Test-Path $layerDir)) { continue }
        Get-ChildItem -Recurse $layerDir -Include "*.java" -File | ForEach-Object {
            $fp = $_.FullName
            if ($writtenPaths.Contains($fp)) { return }   # already written in Phase 1
            $orig = Read-Java $fp
            $upd  = Transform-Content $orig
            if ($orig -ne $upd) {
                [System.IO.File]::WriteAllText($fp, $upd, $utf8NoBom)
                Write-Host "  Updated: $($_.Name)"
            }
        }
    }

    # Files in non-refactored module dirs (NovedadEstadoPaquete, RegistrarEstadoPago, etc.)
    Get-ChildItem $base -Directory | Where-Object {
        $_.Name -notin ([string[]]$modulePrefix.Keys) -and
        $_.Name -notin $standardLayers
    } | ForEach-Object {
        Get-ChildItem -Recurse $_.FullName -Include "*.java" -File | ForEach-Object {
            $fp   = $_.FullName
            $orig = Read-Java $fp
            $upd  = Transform-Content $orig
            if ($orig -ne $upd) {
                [System.IO.File]::WriteAllText($fp, $upd, $utf8NoBom)
                Write-Host "  Updated (other-module): $($_.Name)"
            }
        }
    }
}

# ── Phase 3: Fix JpaRepository naming conflict ────────────────────────────────
Write-Host "=== Phase 3: Fixing JpaRepository naming conflict ===" -ForegroundColor Cyan

$jpaPath = Join-Path $mainBase "infrastructure\liquidacion\persistence\repositories\JpaRepository.java"
if (Test-Path $jpaPath) {
    $c = Read-Java $jpaPath
    # Remove the now-shadowing Spring import
    $c = [regex]::Replace($c,
        "import\s+org\.springframework\.data\.jpa\.repository\.JpaRepository\s*;\r?\n", "")
    # Qualify the Spring interface in the extends clause
    $c = $c -replace "extends JpaRepository<",
                      "extends org.springframework.data.jpa.repository.JpaRepository<"
    [System.IO.File]::WriteAllText($jpaPath, $c, $utf8NoBom)
    Write-Host "  Fixed: JpaRepository.java (uses fully-qualified Spring type)"
}

# ── Phase 4: Remove old module directories ────────────────────────────────────
Write-Host "=== Phase 4: Removing old module directories ===" -ForegroundColor Cyan

foreach ($mod in $modulePrefix.Keys) {
    foreach ($base in $allBases) {
        $modDir = Join-Path $base $mod
        if (Test-Path $modDir) {
            Remove-Item -Recurse -Force $modDir
            Write-Host "  Removed: $modDir"
        }
    }
}

Write-Host ""
Write-Host "Refactoring complete!" -ForegroundColor Green

# ── Summary: list new directory structure ─────────────────────────────────────
Write-Host ""
Write-Host "New structure (main/java/com/logistica):" -ForegroundColor Yellow
foreach ($lyr in $standardLayers) {
    $d = Join-Path $mainBase $lyr
    if (Test-Path $d) {
        Write-Host "  $lyr/"
        Get-ChildItem $d -Directory | ForEach-Object { Write-Host "    $($_.Name)/" }
    }
}

"""
Second refactor: eliminate duplicate class names, create domain/shared/ package.
"""
import os
import re
import shutil

BASE = r"C:\Users\LUIS\IdeaProjects\20261LogisticsInvoice\backend\src\main\java\com\logistica"
TEST = r"C:\Users\LUIS\IdeaProjects\20261LogisticsInvoice\backend\src\test\java\com\logistica"

def p(*parts):
    return os.path.join(*parts)

def read(path):
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()

def write(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

def delete(path):
    if os.path.exists(path):
        os.remove(path)
        print(f"  DELETED: {os.path.basename(path)}")
    else:
        print(f"  NOT FOUND (skip): {path}")

def update(path, *replacements):
    """Apply (old, new) string replacements. Returns True if changed."""
    if not os.path.exists(path):
        print(f"  NOT FOUND: {path}")
        return False
    content = read(path)
    original = content
    for old, new in replacements:
        content = content.replace(old, new)
    if content != original:
        write(path, content)
        print(f"  UPDATED: {os.path.basename(path)}")
        return True
    else:
        print(f"  NO CHANGE: {os.path.basename(path)}")
        return False

def update_regex(path, pattern, replacement):
    if not os.path.exists(path):
        print(f"  NOT FOUND: {path}")
        return False
    content = read(path)
    original = content
    content = re.sub(pattern, replacement, content)
    if content != original:
        write(path, content)
        print(f"  UPDATED (regex): {os.path.basename(path)}")
        return True
    print(f"  NO CHANGE: {os.path.basename(path)}")
    return False

def rename_model(old_file, new_file, *replacements):
    """Read old file, apply replacements, write to new file, delete old."""
    if not os.path.exists(old_file):
        print(f"  NOT FOUND (rename): {old_file}")
        return
    content = read(old_file)
    for old, new in replacements:
        content = content.replace(old, new)
    write(new_file, content)
    if old_file != new_file:
        os.remove(old_file)
    print(f"  RENAMED: {os.path.basename(old_file)} -> {os.path.basename(new_file)}")


# ==============================================================================
print("=" * 70)
print("PHASE 1: Create domain/shared/ package")
print("=" * 70)

shared_enums = p(BASE, "domain", "shared", "enums")
shared_exc   = p(BASE, "domain", "shared", "exceptions")

write(p(shared_enums, "TipoVehiculo.java"), """\
package com.logistica.domain.shared.enums;

import java.util.Arrays;

public enum TipoVehiculo {
    MOTO,
    VAN,
    NHR,
    TURBO;

    public static TipoVehiculo from(String tipo) {
        if (tipo == null) return null;
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(tipo))
                .findFirst()
                .orElse(null);
    }
}
""")
print("  CREATED: domain/shared/enums/TipoVehiculo.java")

write(p(shared_exc, "DomainException.java"), """\
package com.logistica.domain.shared.exceptions;

public abstract class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
""")
print("  CREATED: domain/shared/exceptions/DomainException.java")


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 2: TipoVehiculo – update cierreRuta imports -> shared")
print("=" * 70)

OLD_TV_CR = "import com.logistica.domain.cierreRuta.enums.TipoVehiculo;"
NEW_TV    = "import com.logistica.domain.shared.enums.TipoVehiculo;"

files_tv_cr = [
    p(BASE, "domain", "cierreRuta", "models", "Ruta.java"),
    p(BASE, "infrastructure", "cierreRuta", "adapters", "RutaMapper.java"),
    p(BASE, "application", "cierreRuta", "mappers", "RutaEventMapper.java"),
    p(TEST, "domain", "cierreRuta", "models", "RutaTest.java"),
]
for f in files_tv_cr:
    update(f, (OLD_TV_CR, NEW_TV))

delete(p(BASE, "domain", "cierreRuta", "enums", "TipoVehiculo.java"))


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 3: TipoVehiculo – update contratos imports -> shared")
print("=" * 70)

OLD_TV_CT = "import com.logistica.domain.contratos.enums.TipoVehiculo;"

files_tv_ct = [
    p(BASE, "domain", "contratos", "models", "Vehiculo.java"),
    p(BASE, "domain", "contratos", "models", "Contrato.java"),
    p(BASE, "infrastructure", "contratos", "adapters", "ContratoMapper.java"),
    p(BASE, "infrastructure", "contratos", "persistence", "repositories", "VehiculoRepositoryImpl.java"),
    p(BASE, "application", "contratos", "dtos", "response", "ContratoResponseDTO.java"),
    p(BASE, "application", "contratos", "dtos", "request", "ContratoRequestDTO.java"),
    # tests
    p(TEST, "infrastructure", "contratos", "web", "ContratoControllerIntegrationTest.java"),
    p(TEST, "domain", "contratos", "validators", "PrecioCondicionalValidatorTest.java"),
    p(TEST, "domain", "contratos", "validators", "FechasContratoValidatorTest.java"),
    p(TEST, "application", "contratos", "validators", "PrecioCondicionalValidatorTest.java"),
    p(TEST, "application", "contratos", "validators", "FechasContratoValidatorTest.java"),
    p(TEST, "application", "contratos", "usecases", "contrato", "CrearContratoUseCaseTest.java"),
]
for f in files_tv_ct:
    update(f, (OLD_TV_CT, NEW_TV))

delete(p(BASE, "domain", "contratos", "enums", "TipoVehiculo.java"))


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 4: DomainException – cierreRuta sub-exceptions extend shared")
print("=" * 70)

SHARED_DOM_IMPORT = "import com.logistica.domain.shared.exceptions.DomainException;\n"

cr_exc_dir = p(BASE, "domain", "cierreRuta", "exceptions")
for exc_file in ["EventoDuplicadoException.java", "ParadaInvalidaException.java",
                 "RutaInvalidaException.java", "RutaNotFoundException.java"]:
    path = p(cr_exc_dir, exc_file)
    if os.path.exists(path):
        content = read(path)
        if SHARED_DOM_IMPORT not in content:
            # Insert import after package declaration
            content = re.sub(
                r'(package com\.logistica\.domain\.cierreRuta\.exceptions;\n)',
                r'\1\nimport com.logistica.domain.shared.exceptions.DomainException;\n',
                content
            )
            write(path, content)
            print(f"  ADDED IMPORT: {exc_file}")
        else:
            print(f"  ALREADY OK: {exc_file}")

delete(p(cr_exc_dir, "DomainException.java"))


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 5: DomainException – contratos sub-exceptions extend shared")
print("=" * 70)

ct_exc_dir = p(BASE, "domain", "contratos", "exceptions")
SHARED_DOM_IMPORT_CT = "import com.logistica.domain.shared.exceptions.DomainException;\n"
for exc_file in ["ContratoInvalidoException.java", "ContratoNotFoundException.java",
                 "ContratoYaExisteException.java", "RecursoNoEncontradoException.java",
                 "TransportistaNotFoundException.java"]:
    path = p(ct_exc_dir, exc_file)
    if os.path.exists(path):
        content = read(path)
        if SHARED_DOM_IMPORT_CT not in content:
            content = re.sub(
                r'(package com\.logistica\.domain\.contratos\.exceptions;\n)',
                r'\1\nimport com.logistica.domain.shared.exceptions.DomainException;\n',
                content
            )
            write(path, content)
            print(f"  ADDED IMPORT: {exc_file}")
        else:
            print(f"  ALREADY OK: {exc_file}")

delete(p(ct_exc_dir, "DomainException.java"))


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 6: DomainException – liquidacion extends shared instead of RuntimeException")
print("=" * 70)

liq_dom_exc = p(BASE, "domain", "liquidacion", "exceptions", "DomainException.java")
update(liq_dom_exc,
    ("import org.springframework.http.HttpStatus;\n",
     "import com.logistica.domain.shared.exceptions.DomainException;\nimport org.springframework.http.HttpStatus;\n"),
    ("public abstract class DomainException extends RuntimeException {",
     "public abstract class DomainException extends com.logistica.domain.shared.exceptions.DomainException {"),
)
# Fix the constructor: shared DomainException has (String, Throwable) not (String, HttpStatus)
# liquidacion's DomainException adds HttpStatus on top - keep super(message) call
# The constructor currently is:
#   public DomainException(String message, HttpStatus status) { super(message); ...}
# That's fine - it calls super(String message) which exists in shared.DomainException


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 7: CierreRutaGlobalExceptionHandler – update DomainException import")
print("=" * 70)

update(
    p(BASE, "infrastructure", "cierreRuta", "web", "handlers", "CierreRutaGlobalExceptionHandler.java"),
    ("import com.logistica.domain.cierreRuta.exceptions.DomainException;",
     "import com.logistica.domain.shared.exceptions.DomainException;")
)


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 8: Rename cierreRuta Ruta -> RutaCerrada")
print("=" * 70)

OLD_RUTA_IMPORT_CR = "import com.logistica.domain.cierreRuta.models.Ruta;"
NEW_RUTA_IMPORT_CR = "import com.logistica.domain.cierreRuta.models.RutaCerrada;"

# Rename the model file itself
rename_model(
    p(BASE, "domain", "cierreRuta", "models", "Ruta.java"),
    p(BASE, "domain", "cierreRuta", "models", "RutaCerrada.java"),
    ("public class Ruta {", "public class RutaCerrada {"),
)

# Files that import cierreRuta.models.Ruta
files_ruta_cr = [
    p(BASE, "infrastructure", "cierreRuta", "persistence", "repositories", "RutaRepositoryImpl.java"),
    p(BASE, "infrastructure", "cierreRuta", "adapters", "RutaMapper.java"),
    p(BASE, "domain", "cierreRuta", "services", "ClasificacionRutaService.java"),
    p(BASE, "domain", "cierreRuta", "repositories", "RutaRepository.java"),
    p(BASE, "application", "cierreRuta", "usecases", "ruta", "ProcesarRutaCerradaUseCase.java"),
    p(BASE, "application", "cierreRuta", "usecases", "ruta", "ConsultarRutaUseCase.java"),
    p(BASE, "application", "cierreRuta", "mappers", "RutaResponseMapper.java"),
    p(BASE, "application", "cierreRuta", "mappers", "RutaEventMapper.java"),
    p(TEST, "domain", "services", "ClasificacionRutaServiceTest.java"),
    p(TEST, "application", "usecases", "ProcesarRutaCerradaUseCaseTest.java"),
]
for f in files_ruta_cr:
    if os.path.exists(f):
        content = read(f)
        content = content.replace(OLD_RUTA_IMPORT_CR, NEW_RUTA_IMPORT_CR)
        # Replace type references \bRuta\b -> RutaCerrada (word boundary, case sensitive)
        content = re.sub(r'\bRuta\b', 'RutaCerrada', content)
        write(f, content)
        print(f"  UPDATED: {os.path.basename(f)}")
    else:
        print(f"  NOT FOUND: {f}")


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 9: Rename liquidacion Ruta -> RutaLiquidacion")
print("=" * 70)

OLD_RUTA_IMPORT_LIQ = "import com.logistica.domain.liquidacion.models.Ruta;"
NEW_RUTA_IMPORT_LIQ = "import com.logistica.domain.liquidacion.models.RutaLiquidacion;"

# Rename the model file + inner builder class
rename_model(
    p(BASE, "domain", "liquidacion", "models", "Ruta.java"),
    p(BASE, "domain", "liquidacion", "models", "RutaLiquidacion.java"),
    ("public class Ruta {",        "public class RutaLiquidacion {"),
    ("public static class RutaBuilder {", "public static class RutaLiquidacionBuilder {"),
    ("return new Ruta(",           "return new RutaLiquidacion("),
    ("private Ruta(",              "private RutaLiquidacion("),
)

files_ruta_liq = [
    p(BASE, "application", "liquidacion", "usecases", "CalcularUseCase.java"),
    p(BASE, "infrastructure", "liquidacion", "web", "controllers", "EventoController.java"),
    p(BASE, "infrastructure", "liquidacion", "persistence", "mapper", "LiquidacionRutaMapper.java"),
    p(BASE, "domain", "liquidacion", "strategies", "RecorridoCompletoStrategy.java"),
    p(BASE, "domain", "liquidacion", "strategies", "PorParadaStrategy.java"),
    p(BASE, "domain", "liquidacion", "strategies", "Strategy.java"),
    p(TEST, "infrastructure", "web", "controllers", "EventoControllerTest.java"),
    p(TEST, "domain", "strategies", "PorParadaStrategyTest.java"),
]
for f in files_ruta_liq:
    if os.path.exists(f):
        content = read(f)
        content = content.replace(OLD_RUTA_IMPORT_LIQ, NEW_RUTA_IMPORT_LIQ)
        content = re.sub(r'\bRuta\b', 'RutaLiquidacion', content)
        write(f, content)
        print(f"  UPDATED: {os.path.basename(f)}")
    else:
        print(f"  NOT FOUND: {f}")


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 10: Rename liquidacion Contrato -> ContratoTarifa")
print("=" * 70)

OLD_CT_IMPORT = "import com.logistica.domain.liquidacion.models.Contrato;"
NEW_CT_IMPORT = "import com.logistica.domain.liquidacion.models.ContratoTarifa;"

# Rename the model file
rename_model(
    p(BASE, "domain", "liquidacion", "models", "Contrato.java"),
    p(BASE, "domain", "liquidacion", "models", "ContratoTarifa.java"),
    ("public class Contrato {", "public class ContratoTarifa {"),
    ("return Contrato.builder()", "return ContratoTarifa.builder()"),
)

# Rename ContratoRepository -> ContratoTarifaRepository (domain interface)
rename_model(
    p(BASE, "domain", "liquidacion", "repositories", "ContratoRepository.java"),
    p(BASE, "domain", "liquidacion", "repositories", "ContratoTarifaRepository.java"),
    ("public interface ContratoRepository {", "public interface ContratoTarifaRepository {"),
)

# Files that import liquidacion.models.Contrato
files_contrato_liq = [
    p(BASE, "application", "liquidacion", "usecases", "CalcularUseCase.java"),
    p(BASE, "infrastructure", "liquidacion", "persistence", "repositories", "LiquidacionContratoRepositoryImpl.java"),
    p(BASE, "infrastructure", "liquidacion", "persistence", "mapper", "LiquidacionContratoMapper.java"),
    p(BASE, "domain", "liquidacion", "strategies", "RecorridoCompletoStrategy.java"),
    p(BASE, "domain", "liquidacion", "strategies", "PorParadaStrategy.java"),
    p(BASE, "domain", "liquidacion", "strategies", "Strategy.java"),
    p(TEST, "domain", "strategies", "PorParadaStrategyTest.java"),
]

OLD_CR_REPO = "import com.logistica.domain.liquidacion.repositories.ContratoRepository;"
NEW_CR_REPO = "import com.logistica.domain.liquidacion.repositories.ContratoTarifaRepository;"

for f in files_contrato_liq:
    if os.path.exists(f):
        content = read(f)
        content = content.replace(OLD_CT_IMPORT, NEW_CT_IMPORT)
        content = content.replace(OLD_CR_REPO, NEW_CR_REPO)
        content = re.sub(r'\bContrato\b', 'ContratoTarifa', content)
        write(f, content)
        print(f"  UPDATED: {os.path.basename(f)}")
    else:
        print(f"  NOT FOUND: {f}")

# CalcularUseCase also uses ContratoRepository interface - already handled above


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 11: Rename liquidacion ContratoNotFoundException -> ContratoTarifaNoEncontradaException")
print("=" * 70)

rename_model(
    p(BASE, "domain", "liquidacion", "exceptions", "ContratoNotFoundException.java"),
    p(BASE, "domain", "liquidacion", "exceptions", "ContratoTarifaNoEncontradaException.java"),
    ("public class ContratoNotFoundException extends DomainException {",
     "public class ContratoTarifaNoEncontradaException extends DomainException {"),
    ("public ContratoNotFoundException(UUID idContrato) {",
     "public ContratoTarifaNoEncontradaException(UUID idContrato) {"),
)

OLD_CNF_IMPORT = "import com.logistica.domain.liquidacion.exceptions.ContratoNotFoundException;"
NEW_CNF_IMPORT = "import com.logistica.domain.liquidacion.exceptions.ContratoTarifaNoEncontradaException;"

files_cnf = [
    p(BASE, "application", "liquidacion", "usecases", "CalcularUseCase.java"),
    p(BASE, "infrastructure", "liquidacion", "web", "handlers", "LiquidacionGlobalExceptionHandler.java"),
]
for f in files_cnf:
    if os.path.exists(f):
        content = read(f)
        content = content.replace(OLD_CNF_IMPORT, NEW_CNF_IMPORT)
        content = re.sub(r'\bContratoNotFoundException\b', 'ContratoTarifaNoEncontradaException', content)
        write(f, content)
        print(f"  UPDATED: {os.path.basename(f)}")
    else:
        print(f"  NOT FOUND: {f}")


# ==============================================================================
print("\n" + "=" * 70)
print("PHASE 12: Rename cierreRuta Transportista -> TransportistaRuta")
print("=" * 70)

OLD_TR_IMPORT = "import com.logistica.domain.cierreRuta.models.Transportista;"
NEW_TR_IMPORT = "import com.logistica.domain.cierreRuta.models.TransportistaRuta;"
OLD_TR_REPO   = "import com.logistica.domain.cierreRuta.repositories.TransportistaRepository;"
NEW_TR_REPO   = "import com.logistica.domain.cierreRuta.repositories.TransportistaRutaRepository;"

# Rename the model file
rename_model(
    p(BASE, "domain", "cierreRuta", "models", "Transportista.java"),
    p(BASE, "domain", "cierreRuta", "models", "TransportistaRuta.java"),
    ("public class Transportista {", "public class TransportistaRuta {"),
    ("public Transportista(UUID transportistaId, String nombre) {",
     "public TransportistaRuta(UUID transportistaId, String nombre) {"),
)

# Rename the repository interface
rename_model(
    p(BASE, "domain", "cierreRuta", "repositories", "TransportistaRepository.java"),
    p(BASE, "domain", "cierreRuta", "repositories", "TransportistaRutaRepository.java"),
    ("public interface TransportistaRepository {", "public interface TransportistaRutaRepository {"),
)

# Files that import cierreRuta.models.Transportista
files_tr = [
    p(BASE, "infrastructure", "cierreRuta", "persistence", "repositories", "TransportistaRepositoryImpl.java"),
    p(BASE, "infrastructure", "cierreRuta", "adapters", "TransportistaMapper.java"),
    p(BASE, "domain", "cierreRuta", "repositories", "TransportistaRutaRepository.java"),  # already renamed
    p(BASE, "application", "cierreRuta", "usecases", "ruta", "ProcesarRutaCerradaUseCase.java"),
    p(BASE, "application", "cierreRuta", "mappers", "TransportistaResponseMapper.java"),
    p(BASE, "application", "cierreRuta", "mappers", "TransportistaEventMapper.java"),
    p(BASE, "application", "cierreRuta", "mappers", "RutaEventMapper.java"),
    p(TEST, "application", "usecases", "ProcesarRutaCerradaUseCaseTest.java"),
]
for f in files_tr:
    if os.path.exists(f):
        content = read(f)
        content = content.replace(OLD_TR_IMPORT, NEW_TR_IMPORT)
        content = content.replace(OLD_TR_REPO, NEW_TR_REPO)
        content = re.sub(r'\bTransportista\b', 'TransportistaRuta', content)
        write(f, content)
        print(f"  UPDATED: {os.path.basename(f)}")
    else:
        print(f"  NOT FOUND: {f}")

# Also update RutaCerrada.java which uses Transportista type
ruta_cerrada_path = p(BASE, "domain", "cierreRuta", "models", "RutaCerrada.java")
if os.path.exists(ruta_cerrada_path):
    content = read(ruta_cerrada_path)
    # Add import for TransportistaRuta if not present
    if "import com.logistica.domain.cierreRuta.models.TransportistaRuta;" not in content:
        content = content.replace(
            "import com.logistica.domain.cierreRuta.models.Parada;",
            "import com.logistica.domain.cierreRuta.models.Parada;\nimport com.logistica.domain.cierreRuta.models.TransportistaRuta;"
        )
    content = re.sub(r'\bTransportista\b', 'TransportistaRuta', content)
    write(ruta_cerrada_path, content)
    print(f"  UPDATED: RutaCerrada.java (TransportistaRuta)")

# The `TransportistaResponseDTO` in cierreRuta application layer - was confirmed to rename
print("\n  [NOTE] TransportistaResponseDTO rename skipped - would require DTO layer changes")


# ==============================================================================
print("\n" + "=" * 70)
print("DONE - Run: cd backend && ./gradlew compileJava 2>&1 | head -80")
print("=" * 70)

#!/bin/bash
# =============================================================================
# send-messages.sh
# Envía los tres payloads de prueba a LocalStack desde FUERA del contenedor.
# Uso: bash docs/payloads/send-messages.sh [1|2|3|all]
#   1   → solo ruta-cerrada
#   2   → solo estado-paquete
#   3   → solo contrato-creado
#   all → los tres (default)
# =============================================================================

set -e

ENDPOINT="http://localhost:4566"
REGION="us-east-1"
ACCOUNT="000000000000"
BASE="http://sqs.${REGION}.localhost.localstack.cloud:4566/${ACCOUNT}"
OPTS="--endpoint-url $ENDPOINT --region $REGION --no-sign-request"

# Lee el JSON del archivo y lo compacta en una sola línea
payload() {
  python3 -c "import json,sys; print(json.dumps(json.load(open('$1'))))"
}

send_ruta_cerrada() {
  echo ""
  echo ">>> [1/3] Enviando RUTA_CERRADA a ruta-cerrada-queue..."
  aws sqs send-message \
    --queue-url "$BASE/ruta-cerrada-queue" \
    --message-body "$(payload docs/payloads/ruta-cerrada.json)" \
    $OPTS
  echo "    OK"
}

send_estado_paquete() {
  echo ""
  echo ">>> [2/3] Enviando ESTADO_PAQUETE a novedad-estado-paquete-queue..."
  aws sqs send-message \
    --queue-url "$BASE/novedad-estado-paquete-queue" \
    --message-body "$(payload docs/payloads/estado-paquete.json)" \
    $OPTS
  echo "    OK"
}

send_contrato_creado() {
  echo ""
  echo ">>> [3/3] Enviando CONTRATO_CREADO a contrato-creado-queue..."
  aws sqs send-message \
    --queue-url "$BASE/contrato-creado-queue" \
    --message-body "$(payload docs/payloads/contrato-creado.json)" \
    $OPTS
  echo "    OK"
}

TARGET="${1:-all}"

case "$TARGET" in
  1)   send_ruta_cerrada ;;
  2)   send_estado_paquete ;;
  3)   send_contrato_creado ;;
  all) send_ruta_cerrada; send_estado_paquete; send_contrato_creado ;;
  *)
    echo "Uso: bash send-messages.sh [1|2|3|all]"
    exit 1
    ;;
esac

echo ""
echo "=== Mensajes enviados. Revisa los logs de Spring Boot. ==="

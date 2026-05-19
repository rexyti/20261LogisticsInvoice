#!/bin/bash
set -e

echo "==> Creando colas SQS en LocalStack..."

awslocal sqs create-queue --queue-name ruta-cerrada-queue
awslocal sqs create-queue --queue-name contrato-creado-queue
awslocal sqs create-queue --queue-name novedad-estado-paquete-queue

echo "==> Colas SQS creadas exitosamente:"
awslocal sqs list-queues

package com.logistica.infrastructure.registrarEstadoPago.adapters;

import com.logistica.domain.registrarEstadoPago.exceptions.ErrorComunicacionBancoException;
import com.logistica.domain.registrarEstadoPago.exceptions.PagoRechazadoBancoException;
import com.logistica.domain.registrarEstadoPago.models.VoucherPago;
import com.logistica.domain.registrarEstadoPago.ports.BancoGateway;
import com.logistica.infrastructure.registrarEstadoPago.dtos.BancoPagoRequestDTO;
import com.logistica.infrastructure.registrarEstadoPago.dtos.BancoPagoResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component("bancoGatewayRegistrarEstadoPago")
public class BancoGatewayImpl implements BancoGateway {

    private final RestClient bancoRestClient;

    public BancoGatewayImpl(@Qualifier("bancoPagoRestClient") RestClient bancoRestClient) {
        this.bancoRestClient = bancoRestClient;
    }

    @Override
    public VoucherPago registrarPago(UUID idLiquidacion, BigDecimal monto) {
        log.info("Enviando solicitud de pago al banco. id_liquidacion: {}, monto: {}", idLiquidacion, monto);

        BancoPagoRequestDTO requestBody = new BancoPagoRequestDTO(idLiquidacion, monto);
        BancoPagoResponseDTO response;

        try {
            response = bancoRestClient.post()
                    .uri("/pagos")
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        String body = new String(res.getBody().readAllBytes());

                        log.warn("Pago rechazado por el banco: {}", body);

                        throw new PagoRechazadoBancoException(idLiquidacion, body);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("Error interno del servicio bancario. id_liquidacion: {}", idLiquidacion);
                        throw new ErrorComunicacionBancoException("El servicio bancario respondió con error interno");
                    })
                    .body(BancoPagoResponseDTO.class);
        } catch (ErrorComunicacionBancoException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("Error de conexión con el banco. id_liquidacion: {}", idLiquidacion, ex);
            throw new ErrorComunicacionBancoException(ex.getMessage());
        }

        if (response == null) {
            throw new ErrorComunicacionBancoException("Respuesta vacía del servicio bancario");
        }

        if ("RECHAZADO".equalsIgnoreCase(response.getEstado())) {
            log.warn("Pago rechazado por el banco. id_liquidacion: {}, motivo: {}",
                    idLiquidacion, response.getMotivo());
            throw new PagoRechazadoBancoException(idLiquidacion, response.getMotivo());
        }

        log.info("Pago aprobado por el banco. voucher: {}, id_liquidacion: {}",
                response.getVoucher(), idLiquidacion);

        Instant fechaProcesamiento = OffsetDateTime.parse(response.getFechaProcesamiento()).toInstant();

        return new VoucherPago(response.getVoucher(), fechaProcesamiento);
    }
}

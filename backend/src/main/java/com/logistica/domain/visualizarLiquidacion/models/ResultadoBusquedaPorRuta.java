package com.logistica.domain.visualizarLiquidacion.models;

public sealed interface ResultadoBusquedaPorRuta
        permits ResultadoBusquedaPorRuta.Encontrada,
                ResultadoBusquedaPorRuta.RutaSinLiquidacion,
                ResultadoBusquedaPorRuta.RutaNoExiste {

    record Encontrada(Liquidacion liquidacion) implements ResultadoBusquedaPorRuta {}
    record RutaSinLiquidacion() implements ResultadoBusquedaPorRuta {}
    record RutaNoExiste() implements ResultadoBusquedaPorRuta {}
}
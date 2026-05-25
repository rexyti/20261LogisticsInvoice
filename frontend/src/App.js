import React from 'react';
import { BrowserRouter as Router, Route, Routes, NavLink, Navigate, useNavigate } from 'react-router-dom';
import RutasPage from './modules/cierreRuta/pages/RutasPage';
import RutaDetallePage from './modules/cierreRuta/pages/RutaDetallePage';
import LiquidacionesPage from './modules/liquidacion/pages/LiquidacionesPage';
import LiquidacionDetallePage from './modules/liquidacion/pages/LiquidacionDetallePage';
import PagosPage from './modules/estadoPago/pages/PagosPage';
import ContratosPage from './modules/contratos/pages/ContratosPage';
import ContratoDetallePage from './modules/contratos/pages/ContratoDetallePage';
import SincronizacionPage from './modules/sincronizacion/pages/SincronizacionPage';
import LoginPage from './modules/auth/pages/LoginPage';
import { authService } from './modules/auth/services/authService';
import './App.css';
import './Navigation.css';

const PrivateRoute = ({ children }) => {
  return authService.isAuthenticated() ? children : <Navigate to="/login" replace />;
};

const LogoutButton = () => {
  const navigate = useNavigate();
  const handleLogout = () => {
    authService.logout();
    navigate('/login', { replace: true });
  };
  return (
    <button onClick={handleLogout} className="nav-logout-btn">
      Cerrar sesión
    </button>
  );
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/*"
          element={
            <PrivateRoute>
              <div className="app-container">
                <nav className="main-nav">
                  <div className="nav-logo">
                    <h2>LOGÍSTICA</h2>
                  </div>
                  <ul>
                    <li>
                      <NavLink to="/rutas" className={({ isActive }) => isActive ? 'active' : ''}>
                        Cierre de Rutas
                      </NavLink>
                    </li>
                    <li>
                      <NavLink to="/liquidaciones" className={({ isActive }) => isActive ? 'active' : ''}>
                        Liquidaciones
                      </NavLink>
                    </li>
                    <li>
                      <NavLink to="/pagos" className={({ isActive }) => isActive ? 'active' : ''}>
                        Estado de Pagos
                      </NavLink>
                    </li>
                    <li>
                      <NavLink to="/contratos" className={({ isActive }) => isActive ? 'active' : ''}>
                        Contratos
                      </NavLink>
                    </li>
                    <li>
                      <NavLink to="/sincronizacion" className={({ isActive }) => isActive ? 'active' : ''}>
                        Sincronización
                      </NavLink>
                    </li>
                  </ul>
                  <LogoutButton />
                </nav>

                <main className="page-container">
                  <Routes>
                    <Route path="/rutas" element={<RutasPage />} />
                    <Route path="/rutas/:id" element={<RutaDetallePage />} />
                    <Route path="/liquidaciones" element={<LiquidacionesPage />} />
                    <Route path="/liquidaciones/:id" element={<LiquidacionDetallePage />} />
                    <Route path="/pagos" element={<PagosPage />} />
                    <Route path="/contratos" element={<ContratosPage />} />
                    <Route path="/contratos/:idContrato" element={<ContratoDetallePage />} />
                    <Route path="/sincronizacion" element={<SincronizacionPage />} />
                    <Route path="/" element={<Navigate to="/rutas" replace />} />
                  </Routes>
                </main>
              </div>
            </PrivateRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;

import React from 'react';
import { BrowserRouter as Router, Route, Routes, NavLink, Navigate } from 'react-router-dom';
import RutasPage from './modules/cierreRuta/pages/RutasPage';
import LiquidacionesPage from './modules/liquidacion/pages/LiquidacionesPage';
import PagosPage from './modules/estadoPago/pages/PagosPage';
import LoginPage from './modules/auth/pages/LoginPage';
import { authService } from './modules/auth/services/authService';
import './App.css';
import './Navigation.css';

const PrivateRoute = ({ children }) => {
  return authService.isAuthenticated() ? children : <Navigate to="/login" replace />;
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
                  </ul>
                </nav>

                <main className="page-container">
                  <Routes>
                    <Route path="/rutas" element={<RutasPage />} />
                    <Route path="/liquidaciones" element={<LiquidacionesPage />} />
                    <Route path="/pagos" element={<PagosPage />} />
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

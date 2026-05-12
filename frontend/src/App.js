import React from 'react';
import { BrowserRouter as Router, Route, Routes, NavLink } from 'react-router-dom';
import RutasPage from './modules/cierreRuta/pages/RutasPage';
import './App.css'; // Import global styles
import './Navigation.css'; // Import navigation styles

function App() {
  return (
    <Router>
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
            {/* Add more links here */}
          </ul>
        </nav>

        <main className="page-container">
          <Routes>
            <Route path="/rutas" element={<RutasPage />} />
            <Route path="/" element={<h1>Bienvenido al Sistema de Logística</h1>} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;

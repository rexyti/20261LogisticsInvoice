import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import './LoginPage.css';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await authService.login(email, password);
      localStorage.setItem('authToken', response.data.access_token);
      navigate('/rutas');
    } catch (err) {
      if (!err.response) {
        setError('No se pudo conectar con el servidor. Verifique que Docker esté corriendo (puerto 8080).');
      } else if (err.response.status === 401) {
        setError('Credenciales inválidas. Verifique su usuario y contraseña.');
      } else {
        setError(`Error del servidor: ${err.response.status}`);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1 className="login-title">LOGÍSTICA</h1>
        <p className="login-subtitle">Sistema Financiero — Módulo 3</p>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="login-field">
            <label htmlFor="email">Correo electrónico</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="usuario@ejemplo.com"
              required
            />
          </div>
          <div className="login-field">
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••"
              required
            />
          </div>
          {error && <p className="login-error">{error}</p>}
          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Ingresando...' : 'Ingresar'}
          </button>
        </form>
        <div className="login-hint">
          <p>Credenciales de prueba:</p>
          <code>admin@test.com / 123456</code>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;

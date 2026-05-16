import React, { useState } from 'react';
import { pagoService } from '../services/pagoService';
import './RegistrarEstadoForm.css';

const ESTADOS = ['PENDIENTE', 'EN_PROCESO', 'PAGADO', 'RECHAZADO'];

const now = () => {
  const d = new Date();
  d.setSeconds(0, 0);
  return d.toISOString().slice(0, 16);
};

const emptyForm = () => ({
  id_evento: '',
  id_transaccion_banco: '',
  id_pago: '',
  id_liquidacion: '',
  estado: 'PENDIENTE',
  fecha_evento: now(),
  secuencia: 1,
});

const RegistrarEstadoForm = ({ onSuccess }) => {
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(false);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: name === 'secuencia' ? Number(value) : value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResultado(null);
    try {
      const payload = { ...form, fecha_evento: `${form.fecha_evento}:00` };
      const response = await pagoService.registrarEstadoPago(payload);
      setResultado(response.data);
      setForm(emptyForm());
      onSuccess();
    } catch (err) {
      const msg =
        err.response?.data?.mensaje ||
        err.response?.data?.message ||
        'Error al registrar el evento.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleNuevo = () => {
    setResultado(null);
    setError(null);
    setForm(emptyForm());
  };

  return (
    <div className="ref-panel">
      <h2 className="ref-title">Registrar Estado de Pago</h2>

      {resultado ? (
        <div className="ref-success">
          <p className="ref-success-title">Evento registrado exitosamente</p>
          <div className="ref-result-grid">
            <span>Mensaje</span><span>{resultado.mensaje}</span>
            <span>Procesamiento</span><span>{resultado.procesamiento}</span>
            <span>ID Evento</span><span>{resultado.id_evento}</span>
            <span>ID Transacción</span><span>{resultado.id_transaccion_banco}</span>
          </div>
          <button className="ref-btn-secondary" onClick={handleNuevo}>
            Registrar otro
          </button>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="ref-form">
          <div className="ref-row">
            <div className="ref-field">
              <label>ID Pago *</label>
              <input
                name="id_pago"
                value={form.id_pago}
                onChange={handleChange}
                placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
                required
              />
            </div>
            <div className="ref-field">
              <label>ID Liquidación *</label>
              <input
                name="id_liquidacion"
                value={form.id_liquidacion}
                onChange={handleChange}
                placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
                required
              />
            </div>
          </div>

          <div className="ref-row">
            <div className="ref-field">
              <label>ID Evento *</label>
              <input
                name="id_evento"
                value={form.id_evento}
                onChange={handleChange}
                placeholder="EVT-2026-001"
                required
              />
            </div>
            <div className="ref-field">
              <label>ID Transacción Banco *</label>
              <input
                name="id_transaccion_banco"
                value={form.id_transaccion_banco}
                onChange={handleChange}
                placeholder="TXN-2026-BANK-001"
                required
              />
            </div>
          </div>

          <div className="ref-row">
            <div className="ref-field">
              <label>Estado *</label>
              <select name="estado" value={form.estado} onChange={handleChange}>
                {ESTADOS.map((e) => <option key={e} value={e}>{e}</option>)}
              </select>
            </div>
            <div className="ref-field">
              <label>Secuencia *</label>
              <input
                type="number"
                name="secuencia"
                value={form.secuencia}
                onChange={handleChange}
                min={1}
                required
              />
            </div>
            <div className="ref-field">
              <label>Fecha del Evento *</label>
              <input
                type="datetime-local"
                name="fecha_evento"
                value={form.fecha_evento}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          {error && <p className="ref-error">{error}</p>}

          <div className="ref-actions">
            <button type="submit" className="ref-btn-primary" disabled={loading}>
              {loading ? 'Enviando...' : 'Registrar evento'}
            </button>
          </div>
        </form>
      )}
    </div>
  );
};

export default RegistrarEstadoForm;

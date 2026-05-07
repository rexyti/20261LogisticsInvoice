import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './global.css'; // Use global.css for base styles

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

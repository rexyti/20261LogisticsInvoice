import axios from 'axios';

const login = (email, password) => {
  return axios.post('/auth/login', { email, password });
};

const logout = () => {
  localStorage.removeItem('authToken');
};

const isAuthenticated = () => !!localStorage.getItem('authToken');

export const authService = { login, logout, isAuthenticated };

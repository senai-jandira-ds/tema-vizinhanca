import api from './api';

export const loginCondominium = async (email, password) => {
    
    try {
        const payload = { email, password };
        const response = await api.post('/auth/login/condominium', payload);
        console.log('Login response:', response.data);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const saveToken = (token) => {
    localStorage.setItem('@vizinhanca:token', token);
};

export const getToken = () => {
    return localStorage.getItem('@vizinhanca:token');
};

export const saveCondominiumData = (data) => {
    localStorage.setItem('@vizinhanca:condominium', JSON.stringify(data));
};

export const getCondominiumData = () => {
    const data = localStorage.getItem('@vizinhanca:condominium');
    return data ? JSON.parse(data) : null;
};

export const logout = () => {
    localStorage.removeItem('@vizinhanca:token');
    localStorage.removeItem('@vizinhanca:condominium');
};

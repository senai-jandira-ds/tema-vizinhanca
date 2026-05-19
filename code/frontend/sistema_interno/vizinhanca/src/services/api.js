import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('@vizinhanca:token');
        console.log('Token:', token);
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;

            console.log('URL:', `${config.baseURL}${config.url}`);
            console.log('Método:', config.method);
            console.log('Headers:', config.headers);

        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(

    (response) => {
        console.log(response)
        return response;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;

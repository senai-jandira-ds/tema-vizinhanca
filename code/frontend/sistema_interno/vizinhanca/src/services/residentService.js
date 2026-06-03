import api from "./api";

export const getResidents = async (page = 0, size = 10) => {
    const response = await api.get(`/condominium/resident/me?page=${page}&size=${size}`);
    return response.data;
};

export const getResidentById = async (id) => {
    const response = await api.get(`/resident/${id}`);
    return response.data;
};

export const createResident = async (data) => {
    const response = await api.post("/resident", data);
    return response.data;
};

export const updateResident = async (id, data) => {
    const response = await api.put(`/resident/${id}`, data);
    return response.data;
};

export const deleteResident = async (id) => {
    const response = await api.delete(`/resident/${id}`);
    return response.data;
};
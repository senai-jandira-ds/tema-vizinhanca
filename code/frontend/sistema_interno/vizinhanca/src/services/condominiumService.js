import api from "./api";

export const getCondominiums = async () => {
    const response = await api.get("/condominium");
    return response.data;
};

export const getCondominiumById = async (id) => {
    const response = await api.get(`/condominium/${id}`);
    return response.data;
};

export const createCondominium = async (data) => {
    // Se data for FormData, envia com headers multipart
    if (data instanceof FormData) {
        const response = await api.post("/condominium", data, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    }
    // Caso contrário, envia como JSON normalmente
    const response = await api.post("/condominium", data);
    return response.data;
};

export const updateCondominium = async (id, data) => {
    // Se data for FormData, envia com headers multipart
    if (data instanceof FormData) {
        const response = await api.put(`/condominium/${id}`, data, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    }
    // Caso contrário, envia como JSON normalmente
    const response = await api.put(`/condominium/${id}`, data);
    return response.data;
};

export const deleteCondominium = async (id) => {
    const response = await api.delete(`/condominium/${id}`);
    return response.data;
};

export const getMeCondominium = async () => {
    const response = await api.get("/auth/me/condominium");
    return response.data;
};

export const loginCondominium = async (data) => {
    const response = await api.post("/auth/login/condominium", data);
    return response.data;
};

export const getResidentsByCondominium = async () => {
    const response = await api.get("/condominium/resident/me");
    return response.data;
};

export const getActivitiesByCondominium = async () => {
    const response = await api.get("/condominium/activity/me");
    return response.data;
};
import api from "./api";

export const getBlocks = async () => {
    const response = await api.get("/condominium/block/me");
    return response.data;
};

export const getBlockById = async (id) => {
    const response = await api.get(`/block/${id}`);
    return response.data;
};

export const createBlock = async (data) => {
    const response = await api.post("/block", data);
    return response.data;
};

export const updateBlock = async (id, data) => {
    const response = await api.put(`/block/${id}`, data);
    return response.data;
};

export const deleteBlock = async (id) => {
    const response = await api.delete(`/block/${id}`);
    return response.data;
};

export const getBlocksByCondominium = async () => {
    const response = await api.get("/block/condominium/me");
    return response.data;
};
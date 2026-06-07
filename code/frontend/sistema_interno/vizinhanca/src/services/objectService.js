import api from "./api";

export const getObjects = async (page = 0, size = 10) => {
    const response = await api.get("/condominium/object/me", {
        params: { page, size }
    });
    return response.data;
};

export const getObjectById = async (id) => {
    const response = await api.get(`/object/${id}`);
    return response.data;
};

export const createObject = async (data) => {
    const response = await api.post("/object", data);
    return response.data;
};

export const updateObject = async (id, data) => {
    const response = await api.put(`/object/${id}`, data);
    return response.data;
};

export const deleteObject = async (id) => {
    const response = await api.delete(`/object/${id}`);
    return response.data;
};

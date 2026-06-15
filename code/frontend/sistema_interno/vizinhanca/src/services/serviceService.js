import api from "./api";

export const getServices = async (page = 0, size = 10) => {
    const response = await api.get("/condominium/service/me", {
        params: { page, size }
    });
    return response.data;
};

export const getServiceById = async (id) => {
    const response = await api.get(`/service/${id}`);
    return response.data;
};

export const createService = async (data) => {
    const response = await api.post("/service", data);
    return response.data;
};

export const updateService = async (id, data) => {
    const response = await api.put(`/service/${id}`, data);

    const dedo = await response.json()
    console.log(dedo)
    console.log(dedo)

    return response.data;
};

export const deleteService = async (id) => {
    const response = await api.delete(`/service/${id}`);
    console.log(response)
    return response.data;
};

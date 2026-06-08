import api from "./api";

export const getCategories = async () => {
    const response = await api.get("/category/me");
    return response.data;
};

export const getCategoryById = async (id) => {
    const response = await api.get(`/category/${id}`);
    return response.data;
};

export const createCategory = async (data) => {
    const response = await api.post("/category", data);
    return response.data;
};

export const updateCategory = async (id, data) => {
    const response = await api.put(`/category/${id}`, data);
    return response.data;
};

export const deleteCategory = async (id) => {
    const response = await api.delete(`/category/${id}`);
    return response.data;
};

export const getCategoryTypes = async () => {
    const response = await api.get("/category/type");
    return response.data;
};

export const getCategoryTypeById = async (id) => {
    const response = await api.get(`/category/type/${id}`);
    return response.data;
};

export const getCategoriesByType = async (typeId) => {
    const response = await api.get(`/category/type/${typeId}/categories`);
    return response.data;
};
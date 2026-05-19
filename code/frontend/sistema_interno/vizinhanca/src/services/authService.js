import api from "./api";

export const loginCondominium = async (email, password) => {
    const response = await api.post("/auth/login/condominium", { email, password });
    return response.data;
};

export const getMeCondominium = async () => {
    const response = await api.get("/auth/me/condominium");
    return response.data;
};

export const saveToken = (token) => {
    localStorage.setItem("@vizinhanca:token", token);
};

export const getToken = () => {
    return localStorage.getItem("@vizinhanca:token");
};

export const saveCondominiumData = (data) => {
    localStorage.setItem("@vizinhanca:condominium", JSON.stringify(data));
};

export const getCondominiumData = () => {
    const data = localStorage.getItem("@vizinhanca:condominium");
    return data ? JSON.parse(data) : null;
};

export const logout = () => {
    localStorage.removeItem("@vizinhanca:token");
    localStorage.removeItem("@vizinhanca:condominium");
};
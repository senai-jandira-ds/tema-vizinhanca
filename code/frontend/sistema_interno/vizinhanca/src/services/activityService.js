import api from "./api";

export const getActivities = async () => {
    const response = await api.get("/condominium/activity/me");
    return response.data;
};

export const formatActivityStatus = (status) => {
    const statusMap = {
        OPEN: "Aberto",
        PENDING: "Pendente",
        IN_PROGRESS: "Em andamento",
        FINISHED: "Finalizado",
        CANCELED: "Cancelado",
        ABERTO: "Aberto",
        DISPONÍVEL: "Disponível",
        INDISPONÍVEL: "Indisponível",
        PENDENTE: "Pendente",
        EM_ANDAMENTO: "Em andamento",
        FINALIZADO: "Finalizado",
        CANCELADO: "Cancelado",
        CONCLUIDO: "Concluído",
        EMPRESTADO: "Emprestado"
    };

    return statusMap[status] || status;
};

export const formatActivityType = (type) => {
    const typeMap = {
        SERVICE: "Serviço",
        PUBLICATION: "Publicação",
        REPORT: "Denúncia"
    };
    return typeMap[type] || type;
};

export const formatActivityDate = (date) => {
    if (!date) return "-";
    return new Date(date).toLocaleDateString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
    });
};

export const updateActivity = async (id, data) => {
    const response = await api.put(`/activity/${id}`, data);
    return response.data;
};

export const deleteActivity = async (id) => {
    const response = await api.delete(`/activity/${id}`);
    return response.data;
};
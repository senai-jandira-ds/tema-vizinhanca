import api from "./api";

export const getReports = async () => {
    const response = await api.get("/report");
    return response.data;
};

export const getReportById = async (id) => {
    const response = await api.get(`/report/${id}`);
    return response.data;
};

export const createReport = async (data) => {
    const response = await api.post("/report", data);
    return response.data;
};

export const updateReport = async (id, data) => {
    const response = await api.put(`/report/${id}`, data);
    return response.data;
};

export const deleteReport = async (id) => {
    const response = await api.delete(`/report/${id}`);
    return response.data;
};

export const formatReportDate = (date) => {
    if (!date) return "-";
    return new Date(date).toLocaleDateString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
    });
};

export const formatReportType = (report) => {
    if (report.serviceId) return "Serviço";
    if (report.publication) return "Publicação";
    return "Outro";
};

export const formatStatus = (status) => {
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
        ATIVO: "Ativo",
        CONCLUÍDO: "Concluído"
    };

    return statusMap[status] || status;
};

export const formatReportReason = (reason) => {
    const reasonMap = {
        SPAM: "Spam",
        OFFENSIVE: "Ofensivo",
        INAPPROPRIATE: "Inapropriado",
        FAKE: "Falso",
        VIOLENCE: "Violência"
    };
    return reasonMap[reason] || reason;
};
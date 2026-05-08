export const formatarCPF = (cpf) => {
    if (!cpf) return '';
    // Remove tudo que não é dígito
    const numeros = cpf.replace(/\D/g, '');
    // Verifica se tem 11 dígitos
    if (numeros.length !== 11) return cpf;
    // Aplica a máscara: 000.000.000-00
    return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
};

export const formatarTelefone = (telefone) => {
    if (!telefone) return '';
    const numeros = telefone.replace(/\D/g, '');
    if (numeros.length === 11) {
        // (11) 98765-4321
        return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(7)}`;
    } else if (numeros.length === 10) {
        // (11) 9876-5432
        return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 6)}-${numeros.slice(6)}`;
    }
    return telefone;
};

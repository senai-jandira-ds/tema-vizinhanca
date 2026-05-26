import InputCondominium from "./components/InputCondominium";
import Table from "./components/Table";

import { getCondominiumData } from "../../services/authService";
import { updateCondominium } from "../../services/condominiumService";
import { useState } from "react";

import styles from "./Information.module.css";
import settingsStyles from "./Settings.module.css";

function Informacoes() {
    const [loading, setLoading] = useState(false);
    const data = getCondominiumData();
    const [formData, setFormData] = useState({
        name: data.name || '',
        street: data.address?.street || '',
        number: data.address?.number || '',
        neighborhood: data.address?.neighborhood || '',
        city: data.address?.city || '',
        state: data.address?.state || '',
        amount_blocks: data.amount_blocks || '',
        amount_apartments: data.amount_apartments || ''
    });

    const handleChange = (field, value) => {
        setFormData(prev => ({ ...prev, [field]: value }));
    };

    const handleSave = async () => {
        try {
            setLoading(true);
            const payload = {
                name: formData.name,
                address: {
                    street: formData.street,
                    number: formData.number,
                    neighborhood: formData.neighborhood,
                    city: formData.city,
                    state: formData.state
                },
                amount_blocks: parseInt(formData.amount_blocks),
                amount_apartments: parseInt(formData.amount_apartments),
                photo: data.photo
            };

            await updateCondominium(data.id, payload);
            alert('Alterações salvas com sucesso!');
        } catch (error) {
            console.error('Erro ao salvar alterações:', error);
            alert('Erro ao salvar alterações. Tente novamente.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <div className={styles.condominiumInfo}>

                <div className={styles.condominiumInfoForm}>

                    <span>Nome</span>
                    <InputCondominium
                        value={formData.name}
                        onChange={(e) => handleChange('name', e.target.value)}
                    />

                    <span>Rua</span>
                    <InputCondominium
                        value={formData.street}
                        onChange={(e) => handleChange('street', e.target.value)}
                    />

                    <span>Número</span>
                    <InputCondominium
                        value={formData.number}
                        onChange={(e) => handleChange('number', e.target.value)}
                    />

                    <span>Bairro</span>
                    <InputCondominium
                        value={formData.neighborhood}
                        onChange={(e) => handleChange('neighborhood', e.target.value)}
                    />

                    <span>Cidade</span>
                    <InputCondominium
                        value={formData.city}
                        onChange={(e) => handleChange('city', e.target.value)}
                    />

                    <span>Estado</span>
                    <InputCondominium
                        value={formData.state}
                        onChange={(e) => handleChange('state', e.target.value)}
                    />

                    <span>Blocos no Condomínio</span>
                    <InputCondominium
                        value={formData.amount_blocks}
                        onChange={(e) => handleChange('amount_blocks', e.target.value)}
                    />

                    <span>Apartamentos Totais</span>
                    <InputCondominium
                        value={formData.amount_apartments}
                        onChange={(e) => handleChange('amount_apartments', e.target.value)}
                    />

                    <div className={styles.saveContainer}>
                        <button
                            className={styles.saveButton}
                            onClick={handleSave}
                            disabled={loading}
                        >
                            {loading ? 'Salvando...' : 'Salvar Alterações'}
                        </button>
                    </div>


                </div>

                <div className={styles.condominiumImage}>
                    <img
                        src={data.photo || ""}
                        alt="Logo do Condomínio"
                    />
                </div>

            </div>

        </>
    );
}

export default Informacoes;
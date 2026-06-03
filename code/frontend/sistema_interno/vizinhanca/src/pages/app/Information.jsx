import InputCondominium from "./components/InputCondominium";
import Table from "./components/Table";

import { getCondominiumData } from "../../services/authService";
import { updateCondominium } from "../../services/condominiumService";
import { useState } from "react";
import { toast } from 'react-toastify';

import styles from "./Information.module.css";
import settingsStyles from "./Settings.module.css";

function Informacoes() {
    const [loading, setLoading] = useState(false);
    const data = getCondominiumData();
    const [photoFile, setPhotoFile] = useState(null);
    const [photoPreview, setPhotoPreview] = useState(data.photo || '');
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

    const handlePhotoClick = () => {
        document.getElementById('photoInput').click();
    };

    const handlePhotoChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setPhotoFile(file);

            // Criar preview da imagem
            const reader = new FileReader();
            reader.onloadend = () => {
                setPhotoPreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSave = async () => {
        try {
            setLoading(true);

            const formDataToSend = new FormData();
            formDataToSend.append('name', formData.name);
            formDataToSend.append('street', formData.street);
            formDataToSend.append('number', formData.number);
            formDataToSend.append('neighborhood', formData.neighborhood);
            formDataToSend.append('city', formData.city);
            formDataToSend.append('state', formData.state);
            formDataToSend.append('amount_blocks', parseInt(formData.amount_blocks));
            formDataToSend.append('amount_apartments', parseInt(formData.amount_apartments));

            if (photoFile) {
                formDataToSend.append('photo', photoFile);
            }

            await updateCondominium(data.id, formDataToSend);
            toast.success('Alterações salvas com sucesso!');
        } catch (error) {
            console.error('Erro ao salvar alterações:', error);
            toast.error(error.response?.data?.message || 'Erro ao salvar alterações. Tente novamente.');
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

                    <span>Foto do Condomínio</span>
                    <div className={styles.photoUploadContainer}>
                        <label htmlFor="photoInput" className={styles.photoUploadLabel}>
                            {photoFile ? photoFile.name : 'Clique para selecionar uma foto'}
                        </label>
                        <input
                            id="photoInput"
                            type="file"
                            accept="image/*"
                            onChange={handlePhotoChange}
                            className={styles.fileInput}
                        />
                        {photoFile && (
                            <button
                                type="button"
                                onClick={() => {
                                    setPhotoFile(null);
                                    setPhotoPreview(data.photo || '');
                                }}
                                className={styles.removePhotoButton}
                            >
                                Remover
                            </button>
                        )}
                    </div>

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

                <div className={styles.condominiumImage} onClick={!loading ? handlePhotoClick : undefined}>
                    {loading ? (
                        <div className={styles.loading}>
                            <div className={styles.spinner}></div>
                        </div>
                    ) : (
                        <>
                            {photoPreview ? (
                                <img
                                    src={photoPreview}
                                    alt="Logo do Condomínio"
                                    className={styles.clickableImage}
                                />
                            ) : (
                                <div className={styles.noImagePlaceholder}>
                                    <svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M30 5C16.19 5 5 16.19 5 30C5 43.81 16.19 55 30 55C43.81 55 55 43.81 55 30C55 16.19 43.81 5 30 5ZM30 50.5C18.68 50.5 9.5 41.32 9.5 30C9.5 18.68 18.68 9.5 30 9.5C41.32 9.5 50.5 18.68 50.5 30C50.5 41.32 41.32 50.5 30 50.5ZM40 27.5H35V22.5C35 21.67 34.33 21 33.5 21H26.5C25.67 21 25 21.67 25 22.5V27.5H20C19.17 27.5 18.5 28.17 18.5 29V31C18.5 31.83 19.17 32.5 20 32.5H25V37.5C25 38.33 25.67 39 26.5 39H33.5C34.33 39 35 38.33 35 37.5V32.5H40C40.83 32.5 41.5 31.83 41.5 31V29C41.5 28.17 40.83 27.5 40 27.5Z" fill="#10B765"/>
                                    </svg>
                                </div>
                            )}
                            <div className={styles.imageOverlay}>
                                <span>{photoPreview ? 'Clique para alterar' : 'Clique para adicionar'}</span>
                            </div>
                        </>
                    )}
                </div>

            </div>

        </>
    );
}

export default Informacoes;
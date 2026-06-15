import { useState, useRef, useEffect, useMemo } from "react";
import styles from "./Filter.module.css";
import filterimg from "../../assets/icons/filter.svg";
import { getBlocks } from "../../services/blockService";
import { getCategoryTypes } from "../../services/categoryService";

const DEFAULT_DATA = {
    Status: ["Aberto", "Pendente", "Em andamento", "Concluído", "Cancelado"],
    Tipo: ["Serviço", "Objeto"],
};

export default function Filter({
    filterConfig = null,
    onFilterChange = null,
    dynamicBlocks = false,
    dynamicCategories = false
}) {
    const [isOpen, setIsOpen] = useState(false);
    const [openSection, setOpenSection] = useState(null);
    const [selectedFilters, setSelectedFilters] = useState({});
    const [dynamicData, setDynamicData] = useState({});

    const ref = useRef();

    // Buscar dados dinâmicos da API
    useEffect(() => {
        const fetchDynamicData = async () => {
            const data = {};

            if (dynamicBlocks) {
                try {
                    const blocksData = await getBlocks();
                    const blocks = blocksData.response?.blocks || [];
                    data.Bloco = blocks.map(b => b.block);
                } catch (error) {
                    data.Bloco = [];
                }
            }

            if (dynamicCategories) {
                try {
                    const categoryTypesData = await getCategoryTypes();
                    const types = categoryTypesData.response?.category_types || [];
                    data.Tipo = types.map(t => t.name);
                } catch (error) {
                    data.Tipo = ["Serviço", "Objeto"];
                }
            }

            setDynamicData(data);
        };

        if (dynamicBlocks || dynamicCategories) {
            fetchDynamicData();
        }
    }, [dynamicBlocks, dynamicCategories]);

    // Determinar quais dados usar
    const DATA = useMemo(() => {
        const data = { ...DEFAULT_DATA };

        // Se há filterConfig, usar como base
        if (filterConfig) {
            Object.assign(data, filterConfig);
        }

        // Adicionar dados dinâmicos
        if (dynamicData.Bloco) data.Bloco = dynamicData.Bloco;
        if (dynamicData.Tipo) data.Tipo = dynamicData.Tipo;

        return data;
    }, [filterConfig, dynamicData]);

    function toggleFilter(section, option) {
        setSelectedFilters(prev => {
            const sectionFilters = prev[section] || [];
            let newFilters;

            if (sectionFilters.includes(option)) {
                newFilters = {
                    ...prev,
                    [section]: sectionFilters.filter(f => f !== option)
                };
            } else {
                newFilters = {
                    ...prev,
                    [section]: [...sectionFilters, option]
                };
            }

            // Notificar componente pai sobre mudança nos filtros
            if (onFilterChange) {
                onFilterChange(newFilters);
            }

            return newFilters;
        });
    }

    function isFilterSelected(section, option) {
        return selectedFilters[section]?.includes(option) || false;
    }

    function toggleSection(section) {
        setOpenSection(openSection === section ? null : section);
    }

    useEffect(() => {
        function handleClick(e) {
            if (ref.current && !ref.current.contains(e.target)) {
                setIsOpen(false);
                setOpenSection(null);
            }
        }

        document.addEventListener("mousedown", handleClick);
        return () => document.removeEventListener("mousedown", handleClick);
    }, []);

    return (
        <div className={styles.wrapper} ref={ref}>
            <div className={styles.button} onClick={() => setIsOpen(!isOpen)}>
                Filtros <img src={filterimg} alt="" />
            </div>

            {isOpen && (
                <div className={styles.card}>
                    {Object.entries(DATA).map(([section, options]) => (
                        <div key={section} className={styles.section}>
                            <div
                                className={styles.header}
                                onClick={() => toggleSection(section)}
                            >
                                {section}
                                <svg
                                    className={`${styles.icon} ${openSection === section ? styles.open : ""}`}
                                    width="13"
                                    height="7"
                                    viewBox="0 0 13 7"
                                    fill="none"
                                    xmlns="http://www.w3.org/2000/svg"
                                >
                                    <path
                                        d="M0.75 5.75L5.27829 1.15426C5.53727 0.895196 5.88631 0.75 6.25 0.75C6.61369 0.75 6.96273 0.895196 7.22171 1.15426L11.75 5.75"
                                        stroke="black"
                                        strokeOpacity="0.6"
                                        strokeWidth="1.5"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                    />
                                </svg>
                            </div>

                            {openSection === section && (
                                <div className={styles.content}>
                                    {options.map((opt) => (
                                        <label key={opt} className={styles.option}>
                                            <input
                                                type="checkbox"
                                                checked={isFilterSelected(section, opt)}
                                                onChange={() => toggleFilter(section, opt)}
                                            />
                                            {opt}
                                        </label>
                                    ))}
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

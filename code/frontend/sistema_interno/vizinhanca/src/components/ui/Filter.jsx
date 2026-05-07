import { useState, useRef, useEffect } from "react";
import styles from "./Filter.module.css";
import filterimg from "../../assets/icons/filter.svg";


const DATA = {
    Status: ["Pendente", "Em Andamento", "Concluído"],
    Tipo: ["Disponível", "Emprestado", "Indisponível"],
    Bloco: ["A", "B", "C"],
    Categoria: ["Livro", "Eletrônico"],
};

export default function Filter() {
    const [isOpen, setIsOpen] = useState(false);
    const [openSection, setOpenSection] = useState(null);
    const [selectedFilters, setSelectedFilters] = useState({});

    const ref = useRef();

    function toggleFilter(section, option) {
        setSelectedFilters(prev => {
            const sectionFilters = prev[section] || [];
            if (sectionFilters.includes(option)) {
                return {
                    ...prev,
                    [section]: sectionFilters.filter(f => f !== option)
                };
            } else {
                return {
                    ...prev,
                    [section]: [...sectionFilters, option]
                };
            }
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

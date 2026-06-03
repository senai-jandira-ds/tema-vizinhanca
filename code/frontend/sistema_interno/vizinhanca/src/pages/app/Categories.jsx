import { useEffect, useState } from "react";
import Table from "./components/Table";
import {
  getCategoryTypes,
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory
} from "../../services/categoryService";
import { toast } from 'react-toastify';

import styles from "./Categories.module.css";

function Categories() {
  const [loading, setLoading] = useState(true);
  const [dadosTabela, setDadosTabela] = useState([]);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);

      const tableData = await getCategories();
      const categoriesType =
        await getCategoryTypes();

      if (
        !Array.isArray(
          tableData.response.categories
        )
      ) {
        setDadosTabela([]);
        return;
      }

      const data =
        tableData.response.categories;

      const mappedData = data.map(
        (category, index) => ({
          id:
            category.id?.toString() ||
            (index + 1).toString(),

          nome: category.name || "",

          descricao:
            category.description || "",

          tipo:
            category.type_category.name || "",

          tipo_categoria_id:
            category.type_category.id || ""
        })
      );

      setDadosTabela(mappedData);
    } catch (error) {
      console.error(
        "Erro ao buscar categorias:",
        error
      );

      setDadosTabela([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitCategoria = async (
    id,
    dados
  ) => {
    try {
      if (id) {
        await updateCategory(id, dados);
        toast.success("Categoria atualizada com sucesso!");
      } else {
        await createCategory(dados);
        toast.success("Categoria criada com sucesso!");
      }

      await fetchCategories();
    } catch (error) {
      toast.error(error.response?.data?.message || "Erro ao salvar categoria");
    }
  };

  const handleDeleteCategoria = async (
    id
  ) => {
    try {
      await deleteCategory(id);
      toast.success("Categoria deletada com sucesso!");
      await fetchCategories();
    } catch (error) {
      toast.error(error.response?.data?.message || "Erro ao deletar categoria");
    }
  };

  const colunasTabela = [
    {
      id: "id",
      label: "Nº",
      width: 100
    },

    {
      id: "nome",
      label: "Nome",
      width: 200
    },

    {
      id: "descricao",
      label: "Descrição",
      width: 350
    },

    {
      id: "tipo",
      label: "Tipo",
      width: 150
    }
  ];

  const handleCellClick = (
    valor,
    colunaId,
    linha
  ) => {
    console.log(
      valor,
      colunaId,
      linha
    );
  };

  if (loading) {
    return (
      <div className={styles.loading}>
        <div className={styles.spinner}></div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>
        Categorias cadastradas para os
        moradores
      </h2>

      <div className={styles.tableContainer}>
        <Table
          columns={colunasTabela}
          data={dadosTabela}
          onCellClick={handleCellClick}
          showPagination={true}
          pageSize={7}
          modalType="categoria"
          onCadastrarNovo={() => {}}
          onSubmit={handleSubmitCategoria}
          onDelete={handleDeleteCategoria}
        />
      </div>
    </div>
  );
}

export default Categories;
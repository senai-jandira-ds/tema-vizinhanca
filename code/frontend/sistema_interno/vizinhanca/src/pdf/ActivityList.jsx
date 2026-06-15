import React from 'react';

import {
  View,
  Text,
} from '@react-pdf/renderer';

import PdfLayout from './components/PdfLayout';
import PdfTable from './components/PdfTable';

import { styles } from './styles';

export default function ActivityListPdf({ data }) {

  const columns = [
    {
      key: 'id',
      label: 'Nº',
      width: '8%',
    },
    {
        key: 'nome',
        label: 'Usuário',
        width: '20%',
    },
    {
      key: 'categoria',
      label: 'Categoria',
      width: '18%',
    },
    {
      key: 'descricao',
      label: 'Descrição',
      width: '40%',
    },
    {
      key: 'data',
      label: 'Data',
      width: '20%',
    },
  ];

  return (

    <PdfLayout title="Atividade Geral">

      <View style={styles.card}>

        <Text style={styles.sectionTitle}>
          Atividades do Sistema
        </Text>

        <PdfTable
          columns={columns}
          data={data}
        />

      </View>

    </PdfLayout>
  );
}
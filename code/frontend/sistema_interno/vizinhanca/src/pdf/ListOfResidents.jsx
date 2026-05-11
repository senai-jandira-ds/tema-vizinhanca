import React from 'react';

import {
  View,
  Text,
} from '@react-pdf/renderer';

import PdfLayout from './components/PdfLayout';
import PdfTable from './components/PdfTable';

import { styles } from './styles';

export default function MoradoresPdf({ data }) {

  const columns = [
    {
      key: 'id',
      label: 'Nº',
      width: '10%',
    },
    {
      key: 'nome',
      label: 'Nome',
      width: '30%',
    },
    {
      key: 'cpf',
      label: 'CPF',
      width: '25%',
    },
    {
      key: 'status',
      label: 'Status',
      width: '20%',
    },
  ];

  return (

    <PdfLayout title="Lista de Moradores">

      <View style={styles.card}>

        <Text style={styles.sectionTitle}>
          Moradores
        </Text>

        <PdfTable
          columns={columns}
          data={data}
        />

      </View>

    </PdfLayout>
  );
}
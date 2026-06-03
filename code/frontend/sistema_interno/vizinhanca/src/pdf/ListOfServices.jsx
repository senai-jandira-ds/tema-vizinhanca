import React from 'react';

import {
  View,
  Text,
} from '@react-pdf/renderer';

import PdfLayout from './components/PdfLayout';
import PdfTable from './components/PdfTable';

import { styles } from './styles';

export default function ServicesPdf({ data }) {

  const columns = [
    {
      key: 'id',
      label: 'Nº',
      width: '10%',
    },
    {
      key: 'tipo',
      label: 'Tipo',
      width: '20%',
    },
    {
      key: 'descricao',
      label: 'Descrição',
      width: '45%',
    },
    {
      key: 'solicitante',
      label: 'Solicitante',
      width: '20%',
    },
    {
      key: 'status',
      label: 'Status',
      width: '15%',
    },
  ];

  return (

    <PdfLayout title="Objetos e Pedidos">

      <View style={styles.card}>

        <Text style={styles.sectionTitle}>
          Serviços
        </Text>

        <PdfTable
          columns={columns}
          data={data}
        />

      </View>

    </PdfLayout>
  );
}
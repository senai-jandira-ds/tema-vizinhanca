import React from 'react';

import {
  View,
  Text,
} from '@react-pdf/renderer';

import PdfLayout from './components/PdfLayout';
import PdfTable from './components/PdfTable';

import { styles } from './styles';

export default function ReportsPdf({ data }) {

  const columns = [
    {
      key: 'id',
      label: 'Nº',
      width: '10%',
    },
    {
      key: 'titulo',
      label: 'Título',
      width: '30%',
    },
    {
      key: 'morador',
      label: 'Morador',
      width: '25%',
    },
    {
      key: 'data',
      label: 'Data',
      width: '20%',
    },
    {
      key: 'status',
      label: 'Status',
      width: '15%',
    },
  ];

  return (

    <PdfLayout title="Relatório de Denúncias">

      <View style={styles.card}>

        <Text style={styles.sectionTitle}>
          Denúncias
        </Text>

        <PdfTable
          columns={columns}
          data={data}
        />

      </View>

    </PdfLayout>
  );
}
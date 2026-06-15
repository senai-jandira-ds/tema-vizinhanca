import React from 'react';

import {
  Page,
  View,
  Document,
} from '@react-pdf/renderer';

import { styles } from '../styles';

import PdfHeader from './PdfHeader';
import PdfFooter from './PdfFooter';

export default function PdfLayout({
  title,
  children,
}) {

  return (
    <Document>

      <Page
        size="A4"
        style={styles.page}
      >

        <View style={styles.container}>

          <PdfHeader title={title} />

          {children}

          <PdfFooter />

        </View>

      </Page>

    </Document>
  );
}
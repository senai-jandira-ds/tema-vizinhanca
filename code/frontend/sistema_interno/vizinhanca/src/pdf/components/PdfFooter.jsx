import React from 'react';

import {
  Text,
} from '@react-pdf/renderer';

import { styles } from '../styles';

export default function PdfFooter() {

  return (
    <Text style={styles.footer}>
      Vizinhança © 2026 - Sistema de Gestão de Condomínios
    </Text>
  );
}
import React from 'react';

import {
  View,
  Text,
  Image,
} from '@react-pdf/renderer';

import { styles } from '../styles';

import logoSvg from '../../assets/icons/logo.png'

export default function PdfHeader({ title }) {

  const date = new Date().toLocaleDateString('pt-BR');

  return (
    <View style={styles.card}>

      <View style={styles.header}>

        <View style={styles.logoSection}>

          <Image
            src={logoSvg}
            style={styles.logo}
          />

          <View>

            <Text style={styles.title}>
              Vizinhança
            </Text>

            <Text style={styles.subtitle}>
              {title}
            </Text>

          </View>

        </View>

        <View style={styles.reportInfo}>

          <Text style={styles.reportDate}>
            {date}
          </Text>

          <Text style={styles.reportType}>
            Relatório do Sistema
          </Text>

        </View>

      </View>

    </View>
  );
}
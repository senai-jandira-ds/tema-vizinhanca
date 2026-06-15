// pdf/components/PdfTable.jsx

import React from 'react';

import {
  View,
  Text,
} from '@react-pdf/renderer';

import { styles } from '../styles';

export default function PdfTable({
  columns = [],
  data = [],
}) {

  return (

    <View style={styles.table}>

      <View style={styles.tableHeader}>

        {columns.map((column) => (

          <View
            key={column.key}
            style={{
              width: column.width || '20%',
            }}
          >

            <Text style={styles.headerCell}>
              {column.label}
            </Text>

          </View>

        ))}

      </View>

      {data.map((row, rowIndex) => (

        <View
          key={rowIndex}
          style={styles.tableRow}
        >

          {columns.map((column) => (

            <View
              key={column.key}
              style={{
                width: column.width || '20%',
              }}
            >

              <Text style={styles.cell}>

                {
                  column.render
                    ? column.render(row[column.key], row)
                    : String(row[column.key] ?? '')
                }

              </Text>

            </View>

          ))}

        </View>

      ))}

    </View>
  );
}
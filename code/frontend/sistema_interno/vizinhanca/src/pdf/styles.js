import { StyleSheet } from '@react-pdf/renderer';

export const styles = StyleSheet.create({

  page: {
    backgroundColor: '#f9fafb',
    padding: 32,
    fontFamily: 'Helvetica',
  },

  container: {
    width: '100%',
  },

  card: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e5e7eb',
    padding: 20,
    marginBottom: 20,
  },

  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },

  logoSection: {
    flexDirection: 'row',
    alignItems: 'center',
  },

  logo: {
    width: 60,
    height: 60,
    marginRight: 16,
  },

  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#111827',
  },

  subtitle: {
    fontSize: 10,
    color: '#6b7280',
    marginTop: 4,
  },

  reportInfo: {
    alignItems: 'flex-end',
  },

  reportDate: {
    fontSize: 10,
    color: '#374151',
    marginBottom: 4,
  },

  reportType: {
    fontSize: 9,
    color: '#9ca3af',
  },

  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#111827',
  },

  table: {
    width: '100%',
    borderWidth: 1,
    borderColor: '#e5e7eb',
  },

  tableHeader: {
    flexDirection: 'row',
    backgroundColor: '#f3f4f6',
    borderBottomWidth: 1,
    borderBottomColor: '#e5e7eb',
  },

  tableRow: {
    flexDirection: 'row',
    borderBottomWidth: 1,
    borderBottomColor: '#f3f4f6',
  },

  headerCell: {
    padding: 8,
    fontSize: 9,
    fontWeight: 'bold',
    color: '#374151',
  },

  cell: {
    padding: 8,
    fontSize: 9,
    color: '#111827',
  },

  footer: {
    marginTop: 24,
    textAlign: 'center',
    fontSize: 8,
    color: '#9ca3af',
  },

});
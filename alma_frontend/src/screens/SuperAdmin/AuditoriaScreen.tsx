import React, { useState, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  ActivityIndicator,
  Alert,
  RefreshControl,
} from 'react-native';
import auditoriaService from '../../services/auditoriaService';
import { AuditoriaDTO } from '../../types/api.types';

const AuditoriaScreen = () => {
  const [logs, setLogs] = useState<AuditoriaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchLogs = useCallback(async () => {
    try {
      setError(null);
      const response = await auditoriaService.getAuditoriaReciente(100); // Traer los últimos 100
      setLogs(response);
    } catch (err: any) {
      setError(err.message || 'Ocurrió un error inesperado');
      Alert.alert('Error', err.message || 'No se pudo cargar la auditoría.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    setLoading(true);
    fetchLogs();
  }, [fetchLogs]);

  const onRefresh = () => {
    setRefreshing(true);
    fetchLogs();
  };

  const renderItem = ({ item }: { item: AuditoriaDTO }) => (
    <View style={styles.itemContainer}>
      <Text style={styles.itemTitle}>{item.tipoAccion}</Text>
      <Text>Usuario: {item.nombreAdmin} {item.apellidosAdmin} ({item.emailAdmin})</Text>
      <Text>Fecha: {new Date(item.fechaAccion).toLocaleString()}</Text>
      <Text>Motivo: {item.motivo}</Text>
      <Text style={styles.itemDetails}>Target: {item.tablaAfectada} (ID: {item.idRegistroAfectado})</Text>
    </View>
  );

  if (loading) {
    return (
      <View style={[styles.container, styles.centered]}>
        <ActivityIndicator size="large" color="#ACD467" />
        <Text>Cargando auditoría...</Text>
      </View>
    );
  }

  if (error && !refreshing) {
    return (
      <View style={[styles.container, styles.centered]}>
        <Text style={styles.errorText}>Error: {error}</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <FlatList
        data={logs}
        renderItem={renderItem}
        keyExtractor={item => item.id.toString()}
        ListHeaderComponent={() => <Text style={styles.title}>Logs de Actividad Reciente</Text>}
        ListEmptyComponent={() => (
          <View style={styles.centered}>
            <Text>No hay registros de auditoría.</Text>
          </View>
        )}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#f5f5f5',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#333',
  },
  itemContainer: {
    backgroundColor: '#fff',
    padding: 16,
    marginBottom: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  itemTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 4,
    color: '#005A9C',
  },
  itemDetails: {
    marginTop: 8,
    fontStyle: 'italic',
    fontSize: 12,
    color: '#666',
  },
  errorText: {
    color: 'red',
    fontSize: 16,
  },
});

export default AuditoriaScreen;

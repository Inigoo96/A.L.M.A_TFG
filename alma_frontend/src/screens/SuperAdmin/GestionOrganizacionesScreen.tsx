import React, { useState, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  ActivityIndicator,
  Alert,
  TouchableOpacity,
} from 'react-native';
import organizacionService from '../../services/organizacionService';
import {
  OrganizacionDTO,
  EstadoOrganizacion,
} from '../../types/api.types';
import CambiarEstadoModal from '../../components/SuperAdmin/CambiarEstadoModal';

const tabs: { label: string; status: EstadoOrganizacion }[] = [
  { label: 'Activas', status: EstadoOrganizacion.ACTIVA },
  { label: 'Suspendidas', status: EstadoOrganizacion.SUSPENDIDA },
  { label: 'Baja', status: EstadoOrganizacion.BAJA },
];

const GestionOrganizacionesScreen = () => {
  const [organizaciones, setOrganizaciones] = useState<OrganizacionDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedTab, setSelectedTab] = useState<EstadoOrganizacion>(
    EstadoOrganizacion.ACTIVA,
  );

  // State for the modal
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedOrg, setSelectedOrg] = useState<OrganizacionDTO | null>(null);
  const [targetState, setTargetState] = useState<EstadoOrganizacion | null>(null);

  const fetchOrganizaciones = useCallback(async (status: EstadoOrganizacion) => {
    try {
      setLoading(true);
      setError(null);
      const response = await organizacionService.getOrganizacionesPorEstado(status);
      setOrganizaciones(response);
    } catch (err: any) {
      setError(err.message || 'Ocurrió un error inesperado');
      Alert.alert('Error', err.message || 'No se pudieron cargar las organizaciones.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchOrganizaciones(selectedTab);
  }, [fetchOrganizaciones, selectedTab]);

  const handleOpenModal = (
    org: OrganizacionDTO,
    newState: EstadoOrganizacion,
  ) => {
    setSelectedOrg(org);
    setTargetState(newState);
    setModalVisible(true);
  };

  const handleCloseModal = () => {
    setModalVisible(false);
    setSelectedOrg(null);
    setTargetState(null);
  };

  const handleSubmitStateChange = async (motivo: string) => {
    if (!selectedOrg || !targetState) return;

    try {
      await organizacionService.cambiarEstadoOrganizacion(selectedOrg.id, {
        nuevoEstado: targetState,
        motivo: motivo,
      });
      Alert.alert('Éxito', `La organización "${selectedOrg.nombreOficial}" ha sido actualizada.`);
      handleCloseModal();
      // Refresh the list
      fetchOrganizaciones(selectedTab);
    } catch (err: any) {
      Alert.alert('Error', err.message || 'No se pudo actualizar la organización.');
    }
  };

  const getActionButtons = (item: OrganizacionDTO) => {
    switch (item.estado) {
      case EstadoOrganizacion.ACTIVA:
        return (
          <TouchableOpacity
            style={[styles.actionButton, styles.suspendButton]}
            onPress={() => handleOpenModal(item, EstadoOrganizacion.SUSPENDIDA)}>
            <Text style={styles.actionButtonText}>Suspender</Text>
          </TouchableOpacity>
        );
      case EstadoOrganizacion.SUSPENDIDA:
        return (
          <>
            <TouchableOpacity
              style={[styles.actionButton, styles.activateButton]}
              onPress={() => handleOpenModal(item, EstadoOrganizacion.ACTIVA)}>
              <Text style={styles.actionButtonText}>Activar</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.actionButton, styles.bajaButton]}
              onPress={() => handleOpenModal(item, EstadoOrganizacion.BAJA)}>
              <Text style={styles.actionButtonText}>Dar de Baja</Text>
            </TouchableOpacity>
          </>
        );
      default:
        return null;
    }
  };

  const renderItem = ({ item }: { item: OrganizacionDTO }) => (
    <View style={styles.itemContainer}>
      <Text style={styles.itemTitle}>{item.nombreOficial}</Text>
      <Text>CIF: {item.cif}</Text>
      <Text>Email: {item.emailCorporativo}</Text>
      <Text style={styles.itemStatus}>Estado: {item.estado}</Text>
      <View style={styles.buttonRow}>{getActionButtons(item)}</View>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Gestión de Organizaciones</Text>

      <View style={styles.tabContainer}>
        {tabs.map(tab => (
          <TouchableOpacity
            key={tab.status}
            style={[styles.tab, selectedTab === tab.status && styles.tabActive]}
            onPress={() => setSelectedTab(tab.status)}>
            <Text
              style={[
                styles.tabText,
                selectedTab === tab.status && styles.tabTextActive,
              ]}>
              {tab.label}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {loading ? (
        <View style={styles.centered}>
            <ActivityIndicator size="large" color="#ACD467" />
            <Text>Cargando organizaciones...</Text>
        </View>
      ) : error ? (
        <View style={styles.centered}>
            <Text style={styles.errorText}>Error: {error}</Text>
        </View>
      ) : (
        <FlatList
          data={organizaciones}
          renderItem={renderItem}
          keyExtractor={item => item.id.toString()}
          ListEmptyComponent={() => (
            <View style={styles.centered}>
              <Text>No hay organizaciones en este estado.</Text>
            </View>
          )}
        />
      )}

      <CambiarEstadoModal
        visible={modalVisible}
        onClose={handleCloseModal}
        onSubmit={handleSubmitStateChange}
        organization={selectedOrg}
        newState={targetState}
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
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#333',
  },
  tabContainer: {
    flexDirection: 'row',
    marginBottom: 16,
  },
  tab: {
    flex: 1,
    paddingVertical: 10,
    alignItems: 'center',
    backgroundColor: '#fff',
    borderBottomWidth: 2,
    borderBottomColor: '#ddd',
  },
  tabActive: {
    borderBottomColor: '#ACD467',
  },
  tabText: {
    color: '#666',
  },
  tabTextActive: {
    color: '#333',
    fontWeight: 'bold',
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
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  itemStatus: {
    marginTop: 8,
    fontStyle: 'italic',
    color: '#666',
  },
  errorText: {
    color: 'red',
    fontSize: 16,
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: 12,
  },
  actionButton: {
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 20,
    marginLeft: 10,
  },
  actionButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  suspendButton: {
    backgroundColor: '#f0ad4e',
  },
  activateButton: {
    backgroundColor: '#5cb85c',
  },
  bajaButton: {
    backgroundColor: '#d9534f',
  },
});

export default GestionOrganizacionesScreen;

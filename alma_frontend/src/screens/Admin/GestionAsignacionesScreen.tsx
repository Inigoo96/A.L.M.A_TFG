import React, {useState, useEffect, useCallback} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  RefreshControl,
  Modal,
  ScrollView,
} from 'react-native';
import {Picker} from '@react-native-picker/picker';
import {colors} from '../../theme';
import {
  ProfesionalDetalleDTO,
  PacienteDetalleDTO,
} from '../../types/api.types';
import asignacionService from '../../services/asignacionService';
import profesionalService from '../../services/profesionalService';
import pacienteService from '../../services/pacienteService';
import {styles} from '../../styles/screens/Admin/GestionAsignacionesScreen.styles';

interface Props {
  navigation: any;
}

const GestionAsignacionesScreen: React.FC<Props> = ({navigation}) => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [profesionales, setProfesionales] = useState<ProfesionalDetalleDTO[]>([]);
  const [pacientes, setPacientes] = useState<PacienteDetalleDTO[]>([]);

  // Modal de creación
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedProfesional, setSelectedProfesional] = useState<number | null>(null);
  const [selectedPaciente, setSelectedPaciente] = useState<number | null>(null);
  const [esPrincipal, setEsPrincipal] = useState(true);
  const [creatingAsignacion, setCreatingAsignacion] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      // TODO: Implementar endpoint para obtener todas las asignaciones de la organización
      // Por ahora cargamos solo profesionales y pacientes
      const [profs, pacs] = await Promise.all([
        profesionalService.getProfesionalesDeOrganizacion(),
        pacienteService.getPacientesDetalle(),
      ]);
      setProfesionales(profs.filter(p => p.activo));
      setPacientes(pacs.filter(p => p.activo));
    } catch (error: any) {
      console.error('Error al cargar datos:', error);
      Alert.alert('Error', error.message || 'No se pudieron cargar los datos');
    } finally {
      setLoading(false);
    }
  };

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await loadData();
    setRefreshing(false);
  }, []);

  const handleCreateAsignacion = async () => {
    if (!selectedProfesional || !selectedPaciente) {
      Alert.alert('Error', 'Debes seleccionar un profesional y un paciente');
      return;
    }

    setCreatingAsignacion(true);
    try {
      await asignacionService.createAsignacion({
        profesionalId: selectedProfesional,
        pacienteId: selectedPaciente,
        esPrincipal,
      });

      Alert.alert(
        '✅ Asignación Creada',
        `Se ha asignado correctamente el paciente al profesional.\n` +
          `Tipo: ${esPrincipal ? 'Principal' : 'Secundaria'}`,
      );

      // Reset y cerrar modal
      setShowCreateModal(false);
      setSelectedProfesional(null);
      setSelectedPaciente(null);
      setEsPrincipal(true);
      await loadData();
    } catch (error: any) {
      console.error('Error al crear asignación:', error);
      Alert.alert('Error', error.message || 'No se pudo crear la asignación');
    } finally {
      setCreatingAsignacion(false);
    }
  };

  const renderCreateModal = () => (
    <Modal
      visible={showCreateModal}
      animationType="slide"
      transparent={true}
      onRequestClose={() => setShowCreateModal(false)}>
      <View style={styles.modalOverlay}>
        <View style={styles.modalContent}>
          <ScrollView>
            <Text style={styles.modalTitle}>Nueva Asignación</Text>

            <View style={styles.modalSection}>
              <Text style={styles.modalLabel}>Profesional *</Text>
              <View style={styles.pickerContainer}>
                <Picker
                  selectedValue={selectedProfesional}
                  onValueChange={(value) => setSelectedProfesional(value)}
                  style={styles.picker}>
                  <Picker.Item label="Seleccionar profesional..." value={null} />
                  {profesionales.map((prof) => (
                    <Picker.Item
                      key={prof.idProfesional}
                      label={`${prof.nombre} ${prof.apellidos} ${prof.especialidad ? `- ${prof.especialidad}` : ''}`}
                      value={prof.idProfesional}
                    />
                  ))}
                </Picker>
              </View>
            </View>

            <View style={styles.modalSection}>
              <Text style={styles.modalLabel}>Paciente *</Text>
              <View style={styles.pickerContainer}>
                <Picker
                  selectedValue={selectedPaciente}
                  onValueChange={(value) => setSelectedPaciente(value)}
                  style={styles.picker}>
                  <Picker.Item label="Seleccionar paciente..." value={null} />
                  {pacientes.map((pac) => (
                    <Picker.Item
                      key={pac.idPaciente}
                      label={`${pac.nombre} ${pac.apellidos}`}
                      value={pac.idPaciente}
                    />
                  ))}
                </Picker>
              </View>
            </View>

            <View style={styles.modalSection}>
              <Text style={styles.modalLabel}>Tipo de Asignación</Text>
              <View style={styles.checkboxContainer}>
                <TouchableOpacity
                  style={styles.checkbox}
                  onPress={() => setEsPrincipal(!esPrincipal)}>
                  <View
                    style={[
                      styles.checkboxBox,
                      esPrincipal && styles.checkboxBoxChecked,
                    ]}>
                    {esPrincipal && (
                      <Text style={styles.checkboxCheck}>✓</Text>
                    )}
                  </View>
                  <Text style={styles.checkboxLabel}>
                    Asignación Principal
                  </Text>
                </TouchableOpacity>
              </View>
              <Text style={styles.helpText}>
                {esPrincipal
                  ? 'Este será el profesional principal del paciente'
                  : 'Esta será una asignación secundaria/colaborativa'}
              </Text>
            </View>

            <View style={styles.modalButtons}>
              <TouchableOpacity
                style={[styles.modalButton, styles.modalButtonCancel]}
                onPress={() => {
                  setShowCreateModal(false);
                  setSelectedProfesional(null);
                  setSelectedPaciente(null);
                  setEsPrincipal(true);
                }}
                disabled={creatingAsignacion}>
                <Text style={styles.modalButtonTextCancel}>Cancelar</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[
                  styles.modalButton,
                  styles.modalButtonCreate,
                  creatingAsignacion && styles.buttonDisabled,
                ]}
                onPress={handleCreateAsignacion}
                disabled={creatingAsignacion}>
                {creatingAsignacion ? (
                  <ActivityIndicator color={colors.white} />
                ) : (
                  <Text style={styles.modalButtonTextCreate}>Crear</Text>
                )}
              </TouchableOpacity>
            </View>
          </ScrollView>
        </View>
      </View>
    </Modal>
  );

  const renderEmptyList = () => (
    <View style={styles.emptyContainer}>
      <Text style={styles.emptyTitle}>Sin asignaciones</Text>
      <Text style={styles.emptyText}>
        Crea la primera asignación entre un profesional y un paciente.
      </Text>
      <TouchableOpacity
        style={styles.emptyButton}
        onPress={() => setShowCreateModal(true)}>
        <Text style={styles.emptyButtonText}>➕ Crear Asignación</Text>
      </TouchableOpacity>
    </View>
  );

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={styles.loadingText}>Cargando datos...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>Gestión de Asignaciones</Text>
        <Text style={styles.subtitle}>
          {profesionales.length} profesionales • {pacientes.length} pacientes
        </Text>
      </View>

      {/* Botón crear */}
      <View style={styles.actionBar}>
        <TouchableOpacity
          style={styles.createButton}
          onPress={() => setShowCreateModal(true)}>
          <Text style={styles.createButtonText}>➕ Nueva Asignación</Text>
        </TouchableOpacity>
      </View>

      {/* Info Card */}
      <View style={styles.infoCard}>
        <Text style={styles.infoTitle}>ℹ️ Sobre las Asignaciones</Text>
        <Text style={styles.infoText}>
          • <Text style={styles.infoBold}>Principal:</Text> El profesional principal del paciente{'\n'}
          • <Text style={styles.infoBold}>Secundaria:</Text> Profesionales colaboradores
        </Text>
      </View>

      {/* Lista de profesionales y pacientes disponibles */}
      <ScrollView
        style={styles.content}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            colors={[colors.primary]}
            tintColor={colors.primary}
          />
        }>
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>
            📋 Profesionales Disponibles ({profesionales.length})
          </Text>
          {profesionales.length === 0 ? (
            <Text style={styles.emptyText}>No hay profesionales registrados</Text>
          ) : (
            profesionales.slice(0, 5).map((prof) => (
              <View key={prof.idProfesional} style={styles.itemCard}>
                <Text style={styles.itemName}>
                  {prof.nombre} {prof.apellidos}
                </Text>
                {prof.especialidad && (
                  <Text style={styles.itemDetail}>{prof.especialidad}</Text>
                )}
              </View>
            ))
          )}
          {profesionales.length > 5 && (
            <Text style={styles.moreText}>
              ... y {profesionales.length - 5} más
            </Text>
          )}
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>
            👥 Pacientes Disponibles ({pacientes.length})
          </Text>
          {pacientes.length === 0 ? (
            <Text style={styles.emptyText}>No hay pacientes registrados</Text>
          ) : (
            pacientes.slice(0, 5).map((pac) => (
              <View key={pac.idPaciente} style={styles.itemCard}>
                <Text style={styles.itemName}>
                  {pac.nombre} {pac.apellidos}
                </Text>
                <Text style={styles.itemDetail}>{pac.email}</Text>
              </View>
            ))
          )}
          {pacientes.length > 5 && (
            <Text style={styles.moreText}>
              ... y {pacientes.length - 5} más
            </Text>
          )}
        </View>
      </ScrollView>

      {renderCreateModal()}
    </View>
  );
};

export default GestionAsignacionesScreen;

import React, {useState, useEffect, useCallback} from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  RefreshControl,
  TextInput,
} from 'react-native';
import {colors} from '../../theme';
import {PacienteDetalleDTO} from '../../types/api.types';
import profesionalService from '../../services/profesionalService';
import {calcularEdad} from '../../utils/validation';
import {styles} from '../../styles/screens/Profesional/MisPacientesScreen.styles';

interface Props {
  navigation: any;
}

const MisPacientesScreen: React.FC<Props> = ({navigation}) => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [pacientes, setPacientes] = useState<PacienteDetalleDTO[]>([]);
  const [filteredPacientes, setFilteredPacientes] = useState<
    PacienteDetalleDTO[]
  >([]);
  const [soloActivos, setSoloActivos] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadPacientes();
  }, [soloActivos]);

  useEffect(() => {
    filterPacientes();
  }, [searchQuery, pacientes]);

  const loadPacientes = async () => {
    try {
      setLoading(true);
      const data = await profesionalService.getMisPacientesDetalle(soloActivos);
      setPacientes(data);
      setFilteredPacientes(data);
    } catch (error: any) {
      console.error('Error al cargar pacientes:', error);
      Alert.alert(
        'Error',
        error.message || 'No se pudieron cargar los pacientes',
      );
    } finally {
      setLoading(false);
    }
  };

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await loadPacientes();
    setRefreshing(false);
  }, [soloActivos]);

  const filterPacientes = () => {
    if (!searchQuery.trim()) {
      setFilteredPacientes(pacientes);
      return;
    }

    const query = searchQuery.toLowerCase();
    const filtered = pacientes.filter(
      (p) =>
        p.nombre.toLowerCase().includes(query) ||
        p.apellidos.toLowerCase().includes(query) ||
        p.email.toLowerCase().includes(query) ||
        (p.tarjetaSanitaria &&
          p.tarjetaSanitaria.toLowerCase().includes(query)),
    );
    setFilteredPacientes(filtered);
  };

  const handleToggleFiltro = () => {
    setSoloActivos(!soloActivos);
  };

  const renderPacienteCard = ({item}: {item: PacienteDetalleDTO}) => {
    const edad = item.fechaNacimiento
      ? calcularEdad(item.fechaNacimiento)
      : null;

    return (
      <TouchableOpacity
        style={[styles.card, !item.activo && styles.cardInactive]}
        onPress={() => {
          // TODO: Navegar a detalle del paciente
          Alert.alert(
            'Paciente',
            `${item.nombre} ${item.apellidos}\n${item.email}`,
          );
        }}>
        <View style={styles.cardHeader}>
          <View style={styles.headerLeft}>
            <Text style={styles.pacienteNombre}>
              {item.nombre} {item.apellidos}
            </Text>
            {!item.activo && (
              <View style={styles.inactiveBadge}>
                <Text style={styles.inactiveBadgeText}>Inactivo</Text>
              </View>
            )}
          </View>
          {edad !== null && (
            <View style={styles.edadBadge}>
              <Text style={styles.edadText}>{edad} años</Text>
            </View>
          )}
        </View>

        <View style={styles.cardBody}>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Email:</Text>
            <Text style={styles.infoValue}>{item.email}</Text>
          </View>

          {item.tarjetaSanitaria && (
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>Tarjeta Sanitaria:</Text>
              <Text style={styles.infoValue}>{item.tarjetaSanitaria}</Text>
            </View>
          )}

          {item.fechaNacimiento && (
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>Fecha de Nacimiento:</Text>
              <Text style={styles.infoValue}>
                {new Date(item.fechaNacimiento).toLocaleDateString('es-ES')}
              </Text>
            </View>
          )}

          {item.genero && (
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>Género:</Text>
              <Text style={styles.infoValue}>
                {item.genero.replace(/_/g, ' ')}
              </Text>
            </View>
          )}
        </View>

        <View style={styles.cardFooter}>
          <Text style={styles.footerText}>
            Registrado: {new Date(item.fechaRegistro).toLocaleDateString('es-ES')}
          </Text>
          {item.ultimoAcceso && (
            <Text style={styles.footerText}>
              Último acceso:{' '}
              {new Date(item.ultimoAcceso).toLocaleDateString('es-ES')}
            </Text>
          )}
        </View>
      </TouchableOpacity>
    );
  };

  const renderEmptyList = () => (
    <View style={styles.emptyContainer}>
      <Text style={styles.emptyTitle}>
        {soloActivos
          ? 'No tienes pacientes activos asignados'
          : 'No tienes pacientes asignados'}
      </Text>
      <Text style={styles.emptyText}>
        {soloActivos
          ? 'Los pacientes asignados aparecerán aquí una vez que el administrador los configure.'
          : 'Activa el filtro de "Solo activos" para ver si tienes pacientes activos.'}
      </Text>
    </View>
  );

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={styles.loadingText}>Cargando pacientes...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>Mis Pacientes</Text>
        <Text style={styles.subtitle}>
          {filteredPacientes.length}{' '}
          {filteredPacientes.length === 1 ? 'paciente' : 'pacientes'}
          {searchQuery ? ' encontrados' : soloActivos ? ' activos' : ' totales'}
        </Text>
      </View>

      {/* Barra de búsqueda */}
      <View style={styles.searchContainer}>
        <TextInput
          style={styles.searchInput}
          placeholder="Buscar por nombre, email o tarjeta..."
          value={searchQuery}
          onChangeText={setSearchQuery}
          autoCapitalize="none"
          autoCorrect={false}
        />
      </View>

      {/* Filtros */}
      <View style={styles.filterContainer}>
        <TouchableOpacity
          style={[
            styles.filterButton,
            soloActivos && styles.filterButtonActive,
          ]}
          onPress={handleToggleFiltro}>
          <Text
            style={[
              styles.filterButtonText,
              soloActivos && styles.filterButtonTextActive,
            ]}>
            {soloActivos ? '✓ Solo activos' : 'Mostrar todos'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.refreshButton}
          onPress={() => onRefresh()}>
          <Text style={styles.refreshButtonText}>🔄 Actualizar</Text>
        </TouchableOpacity>
      </View>

      {/* Lista de pacientes */}
      <FlatList
        data={filteredPacientes}
        renderItem={renderPacienteCard}
        keyExtractor={(item) => item.idPaciente.toString()}
        contentContainerStyle={styles.listContent}
        ListEmptyComponent={renderEmptyList}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            colors={[colors.primary]}
            tintColor={colors.primary}
          />
        }
      />
    </View>
  );
};

export default MisPacientesScreen;

import React, {useState, useEffect, useCallback} from 'react';
import {
  View,
  Text,
  ScrollView,
  ActivityIndicator,
  RefreshControl,
} from 'react-native';
import {colors} from '../../theme';
import {ProfesionalEstadisticasDTO} from '../../types/api.types';
import profesionalService from '../../services/profesionalService';
import {styles} from '../../styles/screens/Profesional/MisEstadisticasScreen.styles';

interface Props {
  navigation: any;
}

const MisEstadisticasScreen: React.FC<Props> = ({navigation}) => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [estadisticas, setEstadisticas] = useState<ProfesionalEstadisticasDTO | null>(null);

  useEffect(() => {
    loadEstadisticas();
  }, []);

  const loadEstadisticas = async () => {
    try {
      setLoading(true);
      const data = await profesionalService.getMisEstadisticas();
      setEstadisticas(data);
    } catch (error: any) {
      console.error('Error al cargar estadísticas:', error);
      // No mostramos alert para no molestar, solo log
    } finally {
      setLoading(false);
    }
  };

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await loadEstadisticas();
    setRefreshing(false);
  }, []);

  const calcularPorcentajeCarga = (total: number, maxRecomendado: number = 30): number => {
    if (maxRecomendado === 0) return 0;
    return Math.min((total / maxRecomendado) * 100, 100);
  };

  const getColorByCarga = (porcentaje: number): string => {
    if (porcentaje >= 90) return colors.error;
    if (porcentaje >= 70) return '#FFA500'; // Orange
    return colors.primary;
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={styles.loadingText}>Cargando estadísticas...</Text>
      </View>
    );
  }

  if (!estadisticas) {
    return (
      <View style={styles.errorContainer}>
        <Text style={styles.errorTitle}>Sin datos disponibles</Text>
        <Text style={styles.errorText}>
          No se pudieron cargar tus estadísticas
        </Text>
      </View>
    );
  }

  const porcentajeCarga = calcularPorcentajeCarga(estadisticas.totalPacientesAsignados);
  const colorCarga = getColorByCarga(porcentajeCarga);

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl
          refreshing={refreshing}
          onRefresh={onRefresh}
          colors={[colors.primary]}
          tintColor={colors.primary}
        />
      }>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>Mis Estadísticas</Text>
        <Text style={styles.subtitle}>
          {estadisticas.nombreCompleto}
        </Text>
        {estadisticas.especialidad && (
          <Text style={styles.especialidad}>{estadisticas.especialidad}</Text>
        )}
      </View>

      {/* Estadística Principal - Carga de Trabajo */}
      <View style={styles.mainCard}>
        <Text style={styles.mainCardTitle}>Carga de Trabajo</Text>
        <View style={styles.mainCardContent}>
          <Text style={[styles.mainCardNumber, {color: colorCarga}]}>
            {estadisticas.totalPacientesAsignados}
          </Text>
          <Text style={styles.mainCardLabel}>Pacientes Asignados</Text>
        </View>

        {/* Barra de progreso */}
        <View style={styles.progressBarContainer}>
          <View style={styles.progressBarBackground}>
            <View
              style={[
                styles.progressBarFill,
                {
                  width: `${porcentajeCarga}%`,
                  backgroundColor: colorCarga,
                },
              ]}
            />
          </View>
          <Text style={styles.progressText}>
            {porcentajeCarga.toFixed(0)}% de capacidad recomendada (30 pacientes)
          </Text>
        </View>

        {/* Estado de carga */}
        <View style={styles.estadoCargaContainer}>
          {porcentajeCarga < 50 && (
            <View style={[styles.estadoBadge, {backgroundColor: colors.primary}]}>
              <Text style={styles.estadoBadgeText}>✓ Carga Baja</Text>
            </View>
          )}
          {porcentajeCarga >= 50 && porcentajeCarga < 70 && (
            <View style={[styles.estadoBadge, {backgroundColor: colors.primary}]}>
              <Text style={styles.estadoBadgeText}>✓ Carga Normal</Text>
            </View>
          )}
          {porcentajeCarga >= 70 && porcentajeCarga < 90 && (
            <View style={[styles.estadoBadge, {backgroundColor: '#FFA500'}]}>
              <Text style={styles.estadoBadgeText}>⚠️ Carga Alta</Text>
            </View>
          )}
          {porcentajeCarga >= 90 && (
            <View style={[styles.estadoBadge, {backgroundColor: colors.error}]}>
              <Text style={styles.estadoBadgeText}>🔴 Sobrecarga</Text>
            </View>
          )}
        </View>
      </View>

      {/* Grid de Estadísticas */}
      <View style={styles.statsGrid}>
        {/* Pacientes Activos */}
        <View style={styles.statCard}>
          <View style={[styles.statIcon, {backgroundColor: colors.primary}]}>
            <Text style={styles.statIconText}>✓</Text>
          </View>
          <Text style={styles.statNumber}>{estadisticas.pacientesActivos}</Text>
          <Text style={styles.statLabel}>Pacientes Activos</Text>
        </View>

        {/* Pacientes Inactivos */}
        <View style={styles.statCard}>
          <View style={[styles.statIcon, {backgroundColor: colors.textSecondary}]}>
            <Text style={styles.statIconText}>○</Text>
          </View>
          <Text style={styles.statNumber}>{estadisticas.pacientesInactivos}</Text>
          <Text style={styles.statLabel}>Pacientes Inactivos</Text>
        </View>

        {/* Asignaciones Principales */}
        <View style={styles.statCard}>
          <View style={[styles.statIcon, {backgroundColor: colors.darkGreen}]}>
            <Text style={styles.statIconText}>★</Text>
          </View>
          <Text style={styles.statNumber}>
            {estadisticas.asignacionesPrincipales}
          </Text>
          <Text style={styles.statLabel}>Asignaciones Principales</Text>
        </View>

        {/* Asignaciones Secundarias */}
        <View style={styles.statCard}>
          <View style={[styles.statIcon, {backgroundColor: colors.mediumGreen}]}>
            <Text style={styles.statIconText}>☆</Text>
          </View>
          <Text style={styles.statNumber}>
            {estadisticas.totalPacientesAsignados - estadisticas.asignacionesPrincipales}
          </Text>
          <Text style={styles.statLabel}>Asignaciones Secundarias</Text>
        </View>
      </View>

      {/* Información Adicional */}
      <View style={styles.infoSection}>
        <Text style={styles.infoTitle}>📋 Información Profesional</Text>

        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Email:</Text>
          <Text style={styles.infoValue}>{estadisticas.email}</Text>
        </View>

        {estadisticas.numeroColegiado && (
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Número Colegiado:</Text>
            <Text style={styles.infoValue}>{estadisticas.numeroColegiado}</Text>
          </View>
        )}

        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Organización:</Text>
          <Text style={styles.infoValue}>{estadisticas.nombreOrganizacion}</Text>
        </View>
      </View>

      {/* Recomendaciones */}
      <View style={styles.recommendationsCard}>
        <Text style={styles.recommendationsTitle}>💡 Recomendaciones</Text>

        {porcentajeCarga < 50 && (
          <Text style={styles.recommendationText}>
            • Tienes capacidad para más pacientes{'\n'}
            • Considera hablar con tu administrador sobre nuevas asignaciones
          </Text>
        )}

        {porcentajeCarga >= 50 && porcentajeCarga < 70 && (
          <Text style={styles.recommendationText}>
            • Tu carga de trabajo está equilibrada{'\n'}
            • Continúa con el buen trabajo
          </Text>
        )}

        {porcentajeCarga >= 70 && porcentajeCarga < 90 && (
          <Text style={styles.recommendationText}>
            • Tu carga de trabajo es elevada{'\n'}
            • Considera priorizar pacientes principales{'\n'}
            • Evalúa delegar algunas asignaciones secundarias
          </Text>
        )}

        {porcentajeCarga >= 90 && (
          <Text style={styles.recommendationText}>
            • ⚠️ Tienes sobrecarga de pacientes{'\n'}
            • Es importante hablar con tu administrador{'\n'}
            • Considera redistribuir algunas asignaciones{'\n'}
            • Tu bienestar es prioritario
          </Text>
        )}
      </View>
    </ScrollView>
  );
};

export default MisEstadisticasScreen;

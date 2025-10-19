import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  ScrollView,
  Image,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import authService from '../services/authService';
import {colors, fontSize, spacing, borderRadius} from '../theme';

const DashboardScreen = ({navigation}: any) => {
  const [loading, setLoading] = useState(false);
  const [userEmail, setUserEmail] = useState('');
  const [userType, setUserType] = useState('');

  useEffect(() => {
    loadUserData();
  }, []);

  const loadUserData = async () => {
    const email = await AsyncStorage.getItem('user_email');
    const type = await AsyncStorage.getItem('user_type');
    setUserEmail(email || '');
    setUserType(type || '');
  };

  const getRoleLabel = (role: string): string => {
    const roleLabels: {[key: string]: string} = {
      ADMIN_ORGANIZACION: 'Administrador de Organización',
      PROFESIONAL: 'Profesional',
      PACIENTE: 'Paciente',
      SUPERADMIN: 'Super Administrador',
    };
    return roleLabels[role] || role;
  };

  const handleLogout = async () => {
    Alert.alert(
      'Cerrar sesión',
      '¿Estás seguro de que deseas cerrar sesión?',
      [
        {
          text: 'Cancelar',
          style: 'cancel',
        },
        {
          text: 'Cerrar sesión',
          onPress: async () => {
            setLoading(true);
            try {
              await authService.logout();
              navigation.replace('UserTypeSelection');
            } catch (error) {
              Alert.alert('Error', 'No se pudo cerrar la sesión');
            } finally {
              setLoading(false);
            }
          },
        },
      ],
    );
  };

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* Header */}
        <View style={styles.header}>
          <Image
            source={require('../assets/images/alma_logo.png')}
            style={styles.logo}
            resizeMode="contain"
          />
          <Text style={styles.title}>A.L.M.A.</Text>
          <Text style={styles.subtitle}>Panel Principal</Text>
        </View>

        {/* Welcome Card */}
        <View style={styles.welcomeCard}>
          <Text style={styles.welcomeTitle}>¡Bienvenido!</Text>
          <Text style={styles.welcomeText}>
            Gracias por confiar en nosotros para acompañarte en este proceso
          </Text>
        </View>

        {/* User Info Card */}
        <View style={styles.infoCard}>
          <Text style={styles.cardTitle}>Información de Usuario</Text>

          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Email:</Text>
            <Text style={styles.infoValue}>{userEmail}</Text>
          </View>

          <View style={styles.divider} />

          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Rol:</Text>
            <View style={styles.roleBadge}>
              <Text style={styles.roleBadgeText}>{getRoleLabel(userType)}</Text>
            </View>
          </View>
        </View>

        {/* Coming Soon Card */}
        <View style={styles.comingSoonCard}>
          <Text style={styles.comingSoonTitle}>Próximamente</Text>
          <Text style={styles.comingSoonText}>
            • Gestión de pacientes{'\n'}
            • Recursos de apoyo{'\n'}
            • Chat con profesionales{'\n'}
            • Y mucho más...
          </Text>
        </View>

        {/* Logout Button */}
        <TouchableOpacity
          style={[styles.logoutButton, loading && styles.buttonDisabled]}
          onPress={handleLogout}
          disabled={loading}>
          {loading ? (
            <ActivityIndicator color={colors.white} />
          ) : (
            <Text style={styles.logoutButtonText}>Cerrar Sesión</Text>
          )}
        </TouchableOpacity>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollContent: {
    padding: spacing.lg,
  },
  header: {
    alignItems: 'center',
    marginBottom: spacing.xl,
    marginTop: spacing.lg,
  },
  logo: {
    width: 100,
    height: 100,
    marginBottom: spacing.md,
  },
  title: {
    fontSize: fontSize.xxxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xs,
  },
  subtitle: {
    fontSize: fontSize.lg,
    color: colors.mediumGreen,
  },
  welcomeCard: {
    backgroundColor: colors.primary,
    borderRadius: borderRadius.lg,
    padding: spacing.xl,
    marginBottom: spacing.lg,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  welcomeTitle: {
    fontSize: fontSize.xl,
    fontWeight: 'bold',
    color: colors.white,
    marginBottom: spacing.sm,
  },
  welcomeText: {
    fontSize: fontSize.md,
    color: colors.white,
    lineHeight: 22,
    opacity: 0.95,
  },
  infoCard: {
    backgroundColor: colors.white,
    borderRadius: borderRadius.lg,
    padding: spacing.xl,
    marginBottom: spacing.lg,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
    borderLeftWidth: 4,
    borderLeftColor: colors.primary,
  },
  cardTitle: {
    fontSize: fontSize.lg,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.lg,
  },
  infoRow: {
    marginBottom: spacing.md,
  },
  infoLabel: {
    fontSize: fontSize.sm,
    color: colors.mediumGreen,
    marginBottom: spacing.xs,
  },
  infoValue: {
    fontSize: fontSize.md,
    color: colors.textPrimary,
    fontWeight: '600',
  },
  divider: {
    height: 1,
    backgroundColor: colors.border,
    marginVertical: spacing.md,
  },
  roleBadge: {
    backgroundColor: colors.secondary,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.md,
    borderRadius: borderRadius.md,
    alignSelf: 'flex-start',
  },
  roleBadgeText: {
    fontSize: fontSize.sm,
    color: colors.darkGreen,
    fontWeight: '600',
  },
  comingSoonCard: {
    backgroundColor: colors.white,
    borderRadius: borderRadius.lg,
    padding: spacing.xl,
    marginBottom: spacing.xl,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
    borderLeftWidth: 4,
    borderLeftColor: colors.secondary,
  },
  comingSoonTitle: {
    fontSize: fontSize.lg,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.md,
  },
  comingSoonText: {
    fontSize: fontSize.md,
    color: colors.textSecondary,
    lineHeight: 24,
  },
  logoutButton: {
    backgroundColor: colors.error,
    padding: spacing.md,
    borderRadius: borderRadius.sm,
    alignItems: 'center',
    shadowColor: colors.shadowMedium,
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  logoutButtonText: {
    color: colors.white,
    fontSize: fontSize.md,
    fontWeight: 'bold',
  },
});

export default DashboardScreen;
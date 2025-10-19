import React, {useEffect} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Image,
  Animated,
  Pressable,
  Alert,
} from 'react-native';
import {colors, fontSize, spacing, borderRadius} from '../../theme';

const UserTypeSelectionScreen = ({navigation}: any) => {
  const scaleAnim1 = React.useRef(new Animated.Value(1)).current;
  const scaleAnim2 = React.useRef(new Animated.Value(1)).current;
  const helpScaleAnim = React.useRef(new Animated.Value(1)).current;

  // Animaciones de entrada
  const fadeAnim = React.useRef(new Animated.Value(0)).current;
  const slideAnim1 = React.useRef(new Animated.Value(50)).current;
  const slideAnim2 = React.useRef(new Animated.Value(50)).current;

  useEffect(() => {
    // Animación de entrada de los botones
    Animated.stagger(150, [
      Animated.parallel([
        Animated.timing(fadeAnim, {
          toValue: 1,
          duration: 600,
          useNativeDriver: true,
        }),
        Animated.spring(slideAnim1, {
          toValue: 0,
          tension: 50,
          friction: 7,
          useNativeDriver: true,
        }),
      ]),
      Animated.spring(slideAnim2, {
        toValue: 0,
        tension: 50,
        friction: 7,
        useNativeDriver: true,
      }),
    ]).start();
  }, []);

  const handlePressIn = (scaleAnim: Animated.Value) => {
    Animated.spring(scaleAnim, {
      toValue: 0.95,
      useNativeDriver: true,
    }).start();
  };

  const handlePressOut = (scaleAnim: Animated.Value) => {
    Animated.spring(scaleAnim, {
      toValue: 1,
      friction: 3,
      tension: 40,
      useNativeDriver: true,
    }).start();
  };

  const handleHelpPress = () => {
    Animated.sequence([
      Animated.timing(helpScaleAnim, {
        toValue: 0.9,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.spring(helpScaleAnim, {
        toValue: 1,
        friction: 3,
        useNativeDriver: true,
      }),
    ]).start();

    Alert.alert(
      'Ayuda',
      '• Organización: Si representas a una organización de salud mental y deseas crear cuentas para profesionales y pacientes.\n\n• Profesional o Paciente: Si ya tienes credenciales de acceso proporcionadas por tu organización.',
      [{text: 'Entendido', style: 'default'}]
    );
  };

  return (
    <View style={styles.container}>
      {/* Header compacto */}
      <View style={styles.header}>
        <Image
          source={require('../../assets/images/alma_logo.png')}
          style={styles.logo}
          resizeMode="contain"
        />
        <Text style={styles.title}>A.L.M.A.</Text>
        <Text style={styles.subtitle}>Elige cómo quieres acceder</Text>
      </View>

      {/* Botones grandes y táctiles con animación de entrada */}
      <View style={styles.buttonsContainer}>
        <Animated.View
          style={{
            opacity: fadeAnim,
            transform: [{scale: scaleAnim1}, {translateY: slideAnim1}],
          }}>
          <Pressable
            style={({pressed}) => [
              styles.button,
              styles.buttonPrimary,
              pressed && styles.buttonPressed,
            ]}
            onPressIn={() => handlePressIn(scaleAnim1)}
            onPressOut={() => handlePressOut(scaleAnim1)}
            onPress={() => navigation.navigate('RegisterOrganization')}>
            <View style={styles.iconContainer}>
              <Text style={styles.icon}>🏢</Text>
            </View>
            <View style={styles.buttonContent}>
              <Text style={styles.buttonTitle}>Organización</Text>
              <Text style={styles.buttonSubtitle}>
                Registrar y gestionar cuentas
              </Text>
            </View>
          </Pressable>
        </Animated.View>

        <Animated.View
          style={{
            opacity: fadeAnim,
            transform: [{scale: scaleAnim2}, {translateY: slideAnim2}],
          }}>
          <Pressable
            style={({pressed}) => [
              styles.button,
              styles.buttonSecondary,
              pressed && styles.buttonPressed,
            ]}
            onPressIn={() => handlePressIn(scaleAnim2)}
            onPressOut={() => handlePressOut(scaleAnim2)}
            onPress={() => navigation.navigate('Login')}>
            <View style={styles.iconContainer}>
              <Text style={styles.icon}>👤</Text>
            </View>
            <View style={styles.buttonContent}>
              <Text style={styles.buttonTitle}>Profesional o Paciente</Text>
              <Text style={styles.buttonSubtitle}>
                Accede con tus credenciales
              </Text>
            </View>
          </Pressable>
        </Animated.View>
      </View>

      {/* Footer compacto */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>
          Plataforma de acompañamiento en el duelo
        </Text>
      </View>

      {/* Botón de ayuda con animación */}
      <Animated.View
        style={[
          styles.helpButtonContainer,
          {transform: [{scale: helpScaleAnim}]},
        ]}>
        <TouchableOpacity
          style={styles.helpButton}
          onPress={handleHelpPress}
          activeOpacity={0.8}>
          <Text style={styles.helpButtonText}>?</Text>
        </TouchableOpacity>
      </Animated.View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  header: {
    alignItems: 'center',
    paddingTop: spacing.xl,
    paddingBottom: spacing.lg,
  },
  logo: {
    width: 100,
    height: 100,
    marginBottom: spacing.sm,
  },
  title: {
    fontSize: fontSize.xxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xs,
  },
  subtitle: {
    fontSize: fontSize.md,
    color: colors.mediumGreen,
    textAlign: 'center',
  },
  buttonsContainer: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: spacing.lg,
    gap: spacing.xl, // Aumentado el espacio entre botones
  },
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: spacing.xl,
    borderRadius: borderRadius.xl,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 4},
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 6,
    minHeight: 110, // Ligeramente más alto
  },
  buttonPrimary: {
    backgroundColor: colors.primary,
  },
  buttonSecondary: {
    backgroundColor: colors.white,
    borderWidth: 2,
    borderColor: colors.primary,
  },
  buttonPressed: {
    shadowOpacity: 0.1,
    elevation: 4,
  },
  iconContainer: {
    width: 64,
    height: 64,
    borderRadius: borderRadius.xl,
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: spacing.lg, // Más espacio entre icono y texto
  },
  icon: {
    fontSize: 34, // Icono ligeramente más grande
  },
  buttonContent: {
    flex: 1,
    justifyContent: 'center', // Centrado vertical del texto
  },
  buttonTitle: {
    fontSize: fontSize.lg,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xs,
  },
  buttonSubtitle: {
    fontSize: fontSize.md, // Tamaño aumentado para mejor legibilidad
    color: colors.textSecondary,
    lineHeight: 20,
  },
  footer: {
    paddingVertical: spacing.xl,
    paddingHorizontal: spacing.lg,
    alignItems: 'center',
  },
  footerText: {
    fontSize: fontSize.sm, // Ligeramente más grande
    color: colors.mediumGreen,
    textAlign: 'center',
    fontStyle: 'italic',
  },
  helpButtonContainer: {
    position: 'absolute',
    top: spacing.xl, // Alineado con el paddingTop del header
    right: spacing.lg, // Alineado con el paddingHorizontal de los botones
    zIndex: 999,
  },
  helpButton: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 4,
  },
  helpButtonText: {
    fontSize: fontSize.xl,
    fontWeight: 'bold',
    color: colors.white,
  },
});

export default UserTypeSelectionScreen;
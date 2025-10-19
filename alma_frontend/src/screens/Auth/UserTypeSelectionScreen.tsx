import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Image,
  Animated,
  Pressable,
} from 'react-native';
import {colors, fontSize, spacing, borderRadius} from '../../theme';

const UserTypeSelectionScreen = ({navigation}: any) => {
  const scaleAnim1 = React.useRef(new Animated.Value(1)).current;
  const scaleAnim2 = React.useRef(new Animated.Value(1)).current;

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

      {/* Botones grandes y táctiles */}
      <View style={styles.buttonsContainer}>
        <Animated.View style={{transform: [{scale: scaleAnim1}]}}>
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

        <Animated.View style={{transform: [{scale: scaleAnim2}]}}>
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

      {/* Botón de ayuda */}
      <TouchableOpacity style={styles.helpButton}>
        <Text style={styles.helpButtonText}>?</Text>
      </TouchableOpacity>
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
    width: 80,
    height: 80,
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
    gap: spacing.lg,
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
    minHeight: 100,
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
    width: 60,
    height: 60,
    borderRadius: borderRadius.xl,
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: spacing.md,
  },
  icon: {
    fontSize: 32,
  },
  buttonContent: {
    flex: 1,
  },
  buttonTitle: {
    fontSize: fontSize.lg,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xs,
  },
  buttonSubtitle: {
    fontSize: fontSize.sm,
    color: colors.textSecondary,
    lineHeight: 18,
  },
  footer: {
    paddingVertical: spacing.xl,
    paddingHorizontal: spacing.lg,
    alignItems: 'center',
  },
  footerText: {
    fontSize: fontSize.xs,
    color: colors.mediumGreen,
    textAlign: 'center',
    fontStyle: 'italic',
  },
  helpButton: {
    position: 'absolute',
    top: spacing.xl,
    right: spacing.lg,
    width: 40,
    height: 40,
    borderRadius: 20,
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
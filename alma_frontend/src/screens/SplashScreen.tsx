import React, {useEffect, useRef} from 'react';
import {View, Text, StyleSheet, Image, Animated} from 'react-native';
import authService from '../services/authService';
import {colors, fontSize, spacing} from '../theme';

const SplashScreen = ({navigation}: any) => {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const scaleAnim = useRef(new Animated.Value(0.8)).current;

  useEffect(() => {
    // Animación de entrada
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 1000,
        useNativeDriver: true,
      }),
      Animated.spring(scaleAnim, {
        toValue: 1,
        tension: 50,
        friction: 7,
        useNativeDriver: true,
      }),
    ]).start();

    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      // Esperar 2 segundos para mostrar la pantalla
      await new Promise<void>(resolve => setTimeout(resolve, 2000));

      const isAuthenticated = await authService.isAuthenticated();

      if (isAuthenticated) {
        navigation.replace('Dashboard');
      } else {
        navigation.replace('UserTypeSelection');
      }
    } catch (error) {
      console.error('Error al verificar autenticación:', error);
      navigation.replace('UserTypeSelection');
    }
  };

  return (
    <View style={styles.container}>
      <Animated.View
        style={[
          styles.content,
          {
            opacity: fadeAnim,
            transform: [{scale: scaleAnim}],
          },
        ]}>
        <Image
          source={require('../assets/images/alma_logo.png')}
          style={styles.logo}
          resizeMode="contain"
        />
        <Text style={styles.title}>A.L.M.A.</Text>
        <Text style={styles.subtitle}>Acompañamiento en el duelo</Text>

        {/* Indicador de carga */}
        <View style={styles.loadingContainer}>
          <View style={styles.loadingDot} />
          <View style={[styles.loadingDot, styles.loadingDotDelay1]} />
          <View style={[styles.loadingDot, styles.loadingDotDelay2]} />
        </View>
      </Animated.View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    justifyContent: 'center',
    alignItems: 'center',
  },
  content: {
    alignItems: 'center',
    padding: spacing.lg,
  },
  logo: {
    width: 200,
    height: 200,
    marginBottom: spacing.xl,
  },
  title: {
    fontSize: fontSize.xxxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.sm,
  },
  subtitle: {
    fontSize: fontSize.lg,
    color: colors.mediumGreen,
    marginBottom: spacing.xl,
    textAlign: 'center',
  },
  loadingContainer: {
    flexDirection: 'row',
    marginTop: spacing.xl,
    gap: spacing.sm,
  },
  loadingDot: {
    width: 12,
    height: 12,
    borderRadius: 6,
    backgroundColor: colors.primary,
    opacity: 0.4,
  },
  loadingDotDelay1: {
    opacity: 0.7,
  },
  loadingDotDelay2: {
    opacity: 1,
  },
});

export default SplashScreen;
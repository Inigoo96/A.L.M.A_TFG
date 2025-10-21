import React, {useEffect} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Image,
  Animated,
  Pressable,
  Alert,
} from 'react-native';
import {styles} from '../../styles/screens/Auth/UserTypeSelectionScreen.styles';

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

export default UserTypeSelectionScreen;
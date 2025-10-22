import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Image,
  Pressable,
  Alert,
  ScrollView,
} from 'react-native';
import {styles} from '../../styles/screens/Auth/UserTypeSelectionScreen.styles';

const UserTypeSelectionScreen = ({navigation}: any) => {
  const handleHelpPress = () => {
    Alert.alert(
      'Ayuda',
      '• Organización: Si representas a una organización de salud mental y deseas crear cuentas para profesionales y pacientes.\n\n• Profesional sanitario o paciente: Si ya tienes credenciales de acceso proporcionadas por tu organización.',
      [{text: 'Entendido', style: 'default'}]
    );
  };

  return (
    <View style={styles.container}>
      {/* Header con logo */}
      <View style={styles.header}>
        <Image
          source={require('../../assets/images/alma_logo.png')}
          style={styles.logo}
          resizeMode="contain"
        />
      </View>

      {/* Botón de ayuda */}
      <View style={styles.helpButtonContainer}>
        <TouchableOpacity
          style={styles.helpButton}
          onPress={handleHelpPress}
          activeOpacity={0.7}>
          <Text style={styles.helpButtonText}>?</Text>
        </TouchableOpacity>
      </View>

      {/* Título central */}
      <View style={styles.titleContainer}>
        <Text style={styles.mainTitle}>Elige el modo de acceso</Text>
      </View>

      {/* Tarjetas */}
      <ScrollView
        style={styles.cardsContainer}
        showsVerticalScrollIndicator={false}>

        {/* Tarjeta 1: Organización */}
        <Pressable
          style={({pressed}) => [
            styles.card,
            pressed && styles.cardPressed,
          ]}
          onPress={() => navigation.navigate('RegisterOrganizationForm')}>
          {/* Imagen de hospital */}
          <View style={styles.cardImage}>
            <Text style={{fontSize: 80, textAlign: 'center', marginTop: 20}}>🏥</Text>
          </View>

          {/* Contenido */}
          <View style={styles.cardContent}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardTitle}>Organización</Text>
              <TouchableOpacity
                style={styles.accessButton}
                onPress={() => navigation.navigate('RegisterOrganizationForm')}>
                <Text style={styles.accessButtonText}>Acceder</Text>
              </TouchableOpacity>
            </View>
            <Text style={styles.cardDescription}>
              Registra y gestiona cuentas
            </Text>
          </View>
        </Pressable>

        {/* Tarjeta 2: Profesional sanitario o paciente */}
        <Pressable
          style={({pressed}) => [
            styles.card,
            pressed && styles.cardPressed,
          ]}
          onPress={() => navigation.navigate('Login')}>
          {/* Imagen de médico y paciente */}
          <View style={styles.cardImage}>
            <Text style={{fontSize: 80, textAlign: 'center', marginTop: 20}}>👨‍⚕️🤝🧑</Text>
          </View>

          {/* Contenido */}
          <View style={styles.cardContent}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardTitle}>Profesional sanitario o paciente</Text>
              <TouchableOpacity
                style={styles.accessButton}
                onPress={() => navigation.navigate('Login')}>
                <Text style={styles.accessButtonText}>Acceder</Text>
              </TouchableOpacity>
            </View>
            <Text style={styles.cardDescription}>
              Inicia sesión con tus datos de acceso
            </Text>
          </View>
        </Pressable>
      </ScrollView>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>
          A.L.M.A. – Programa de acompañamiento en el duelo
        </Text>
      </View>
    </View>
  );
};

export default UserTypeSelectionScreen;
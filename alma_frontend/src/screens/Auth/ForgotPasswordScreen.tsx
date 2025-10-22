import React, {useState} from 'react';
import {
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import {
  Container,
  Header,
  HeartIcon,
  Separator,
  Title,
  Card,
  CardTitle,
  CardSubtitle,
  Illustration,
  Form,
  InputLabel,
  InputContainer,
  StyledInput,
  SendButton,
  ButtonText,
  BackButton,
  BackButtonText,
  theme
} from '../../styles/screens/Auth/ForgotPasswordScreen.styles';

const ForgotPasswordScreen = ({navigation}: any) => {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const validateEmail = (email: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleResetPassword = async () => {
    if (!validateEmail(email)) {
      Alert.alert('Error', 'Por favor, introduce un email válido');
      return;
    }

    setIsLoading(true);

    try {
      // TODO: Implementar la llamada al servicio de reseteo de contraseña
      // await authService.forgotPassword(email);
      
      // Simulating a network request
      await new Promise(resolve => setTimeout(resolve, 1000));

      Alert.alert(
        'Correo Enviado',
        'Si la dirección de correo está registrada, recibirás instrucciones para restablecer tu contraseña.',
        [
          {
            text: 'OK',
            onPress: () => navigation.navigate('Login'),
          },
        ],
      );
    } catch (error: any) {
      Alert.alert('Error', error.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={{flex: 1}}>
        <Container>
            <Header>
                <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
            </Header>
            <Separator/>
            <ScrollView contentContainerStyle={{padding: 16}} keyboardShouldPersistTaps="handled">
                <Title>Recuperar Contraseña</Title>

                <Card>
                    <CardTitle>¿Olvidaste tu contraseña?</CardTitle>
                    <CardSubtitle>Introduce tu correo y te enviaremos instrucciones para restablecerla</CardSubtitle>
                    <Illustration>
                        <Icon name="account-question-outline" size={80} color={theme.primaryGreen} />
                    </Illustration>
                </Card>

                <Form style={{marginTop: 24}}>
                    <InputLabel>Email</InputLabel>
                    <InputContainer>
                        <StyledInput 
                            placeholder="Introduce tu Email"
                            placeholderTextColor={theme.green}
                            value={email}
                            onChangeText={setEmail}
                            keyboardType="email-address"
                            autoCapitalize="none"
                            editable={!isLoading}
                        />
                    </InputContainer>
                </Form>

                <SendButton onPress={handleResetPassword} disabled={isLoading}>
                    {isLoading ? <ActivityIndicator color={theme.black} /> : <ButtonText>Enviar</ButtonText>}
                </SendButton>

                <BackButton onPress={() => navigation.goBack()} disabled={isLoading}>
                    <BackButtonText>Volver</BackButtonText>
                </BackButton>
            </ScrollView>
        </Container>
    </KeyboardAvoidingView>
  );
};

export default ForgotPasswordScreen;

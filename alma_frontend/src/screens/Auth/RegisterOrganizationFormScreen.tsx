import React, {useState} from 'react';
import {
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Alert,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import {
  isValidCIF,
  isValidNumeroSeguridadSocial,
  isValidEmailCorporativo,
  isValidCodigoREGCESS,
} from '../../utils/validation';
import {
  Container,
  Header,
  HeartIcon,
  Separator,
  Title,
  InputCard,
  InputLabel,
  InputWrapper,
  StyledInput,
  NextButton,
  ButtonText,
  BackButton,
  BackButtonText,
  ErrorText,
  FileUploadButton,
  FileUploadButtonText,
  FileNameText,
  theme,
} from '../../styles/screens/Auth/RegisterOrganizationFormScreen.styles';

const RegisterOrganizationFormScreen = ({navigation}: any) => {
  const [nombreOficial, setNombreOficial] = useState('');
  const [cif, setCif] = useState('');
  const [emailCorporativo, setEmailCorporativo] = useState('');
  const [telefonoContacto, setTelefonoContacto] = useState('');
  const [direccion, setDireccion] = useState('');
  const [numeroSeguridadSocial, setNumeroSeguridadSocial] = useState('');
  const [codigoRegcess, setCodigoRegcess] = useState('');
  
  const [docCif, setDocCif] = useState<any>(null);
  const [docSegSocial, setDocSegSocial] = useState<any>(null);

  const [errors, setErrors] = useState<any>({});

  const handleFilePick = (setter: Function) => {
    // Placeholder function
    Alert.alert("Función no implementada", "La subida de archivos se implementará en el futuro.");
    // In a real implementation, you would use a document picker library
    // For now, we can simulate a file pick
    setter({name: 'documento_simulado.pdf'});
  };

  const validate = () => {
    const newErrors: any = {};

    // Validar nombre oficial
    if (!nombreOficial.trim()) {
      newErrors.nombreOficial = 'El nombre es obligatorio';
    }

    // Validar CIF
    if (!cif.trim()) {
      newErrors.cif = 'El CIF es obligatorio';
    } else if (!isValidCIF(cif)) {
      newErrors.cif = 'El CIF no es válido. Verifica el formato y dígito de control';
    }

    // Validar email corporativo
    if (!emailCorporativo.trim()) {
      newErrors.emailCorporativo = 'El email es obligatorio';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailCorporativo)) {
      newErrors.emailCorporativo = 'El formato del email no es válido';
    } else if (!isValidEmailCorporativo(emailCorporativo)) {
      newErrors.emailCorporativo = 'Debe usar un email corporativo, no dominios públicos (gmail, hotmail, etc.)';
    }

    // Validar teléfono
    if (!telefonoContacto.trim()) {
      newErrors.telefonoContacto = 'El teléfono es obligatorio';
    }

    // Validar dirección
    if (!direccion.trim()) {
      newErrors.direccion = 'La dirección es obligatoria';
    }

    // Validar número de Seguridad Social
    if (!numeroSeguridadSocial.trim()) {
      newErrors.numeroSeguridadSocial = 'El número de la Seguridad Social es obligatorio';
    } else if (!isValidNumeroSeguridadSocial(numeroSeguridadSocial)) {
      newErrors.numeroSeguridadSocial = 'El número de la Seguridad Social no es válido. Formato: PP/NNNNNNNN/DD (12 dígitos)';
    }

    // Validar código REGCESS
    if (!codigoRegcess.trim()) {
      newErrors.codigoRegcess = 'El código REGCESS es obligatorio';
    } else if (!isValidCodigoREGCESS(codigoRegcess)) {
      newErrors.codigoRegcess = 'El código REGCESS no tiene un formato válido';
    }

    // Validar documentos (OBLIGATORIOS)
    if (!docCif) {
      newErrors.docCif = 'El documento CIF es obligatorio';
    }
    if (!docSegSocial) {
      newErrors.docSegSocial = 'El documento de la Seguridad Social es obligatorio';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = () => {
    if (validate()) {
      const organizationData = {
        nombreOficial,
        cif,
        emailCorporativo,
        telefonoContacto,
        direccion,
        numeroSeguridadSocial,
        codigoRegcess,
        // Pass file data if needed, for now just names
        docCifName: docCif?.name,
        docSegSocialName: docSegSocial?.name,
      };
      navigation.navigate('RegisterAdminOrg', {organizationData});
    }
  };

  return (
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={{flex: 1}}>
      <Container>
        <Header>
          <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
        </Header>
        <Separator />
        <ScrollView contentContainerStyle={{padding: 24}} keyboardShouldPersistTaps="handled">
          <Title>Registro de Organización</Title>

          <InputCard>
            <InputLabel>Nombre Oficial</InputLabel>
            <InputWrapper>
              <Icon name="domain" size={20} color={theme.green} />
              <StyledInput value={nombreOficial} onChangeText={setNombreOficial} />
            </InputWrapper>
            {errors.nombreOficial && <ErrorText>{errors.nombreOficial}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>CIF</InputLabel>
            <InputWrapper>
              <Icon name="file-document-outline" size={20} color={theme.green} />
              <StyledInput value={cif} onChangeText={setCif} maxLength={9} autoCapitalize="characters" />
            </InputWrapper>
            {errors.cif && <ErrorText>{errors.cif}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Email Corporativo</InputLabel>
            <InputWrapper>
              <Icon name="email-outline" size={20} color={theme.green} />
              <StyledInput value={emailCorporativo} onChangeText={setEmailCorporativo} keyboardType="email-address" autoCapitalize="none" />
            </InputWrapper>
            {errors.emailCorporativo && <ErrorText>{errors.emailCorporativo}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Teléfono de Contacto</InputLabel>
            <InputWrapper>
              <Icon name="phone-outline" size={20} color={theme.green} />
              <StyledInput value={telefonoContacto} onChangeText={setTelefonoContacto} keyboardType="phone-pad" />
            </InputWrapper>
            {errors.telefonoContacto && <ErrorText>{errors.telefonoContacto}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Dirección</InputLabel>
            <InputWrapper>
              <Icon name="map-marker-outline" size={20} color={theme.green} />
              <StyledInput value={direccion} onChangeText={setDireccion} />
            </InputWrapper>
            {errors.direccion && <ErrorText>{errors.direccion}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Número de Seguridad Social</InputLabel>
            <InputWrapper>
              <Icon name="shield-account-outline" size={20} color={theme.green} />
              <StyledInput value={numeroSeguridadSocial} onChangeText={setNumeroSeguridadSocial} />
            </InputWrapper>
            {errors.numeroSeguridadSocial && <ErrorText>{errors.numeroSeguridadSocial}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Código REGCESS</InputLabel>
            <InputWrapper>
              <Icon name="barcode" size={20} color={theme.green} />
              <StyledInput value={codigoRegcess} onChangeText={setCodigoRegcess} />
            </InputWrapper>
            {errors.codigoRegcess && <ErrorText>{errors.codigoRegcess}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Documentación (OBLIGATORIA)</InputLabel>
            <FileUploadButton onPress={() => handleFilePick(setDocCif)}>
                <Icon name="file-upload-outline" size={20} color={theme.black} />
                <FileUploadButtonText>Adjuntar CIF *</FileUploadButtonText>
            </FileUploadButton>
            {docCif && <FileNameText>{docCif.name}</FileNameText>}
            {errors.docCif && <ErrorText>{errors.docCif}</ErrorText>}

            <FileUploadButton style={{marginTop: 12}} onPress={() => handleFilePick(setDocSegSocial)}>
                <Icon name="file-upload-outline" size={20} color={theme.black} />
                <FileUploadButtonText>Adjuntar Doc. Seguridad Social *</FileUploadButtonText>
            </FileUploadButton>
            {docSegSocial && <FileNameText>{docSegSocial.name}</FileNameText>}
            {errors.docSegSocial && <ErrorText>{errors.docSegSocial}</ErrorText>}
          </InputCard>

          <NextButton onPress={handleNext}>
            <ButtonText>Siguiente</ButtonText>
          </NextButton>

          <BackButton onPress={() => navigation.goBack()}>
            <BackButtonText>Volver</BackButtonText>
          </BackButton>
        </ScrollView>
      </Container>
    </KeyboardAvoidingView>
  );
};

export default RegisterOrganizationFormScreen;

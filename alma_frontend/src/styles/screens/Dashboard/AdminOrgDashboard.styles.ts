import styled from 'styled-components/native';
import {View, Text, TouchableOpacity, Image} from 'react-native';

export const theme = {
  primaryGreen: '#ACD467',
  lightGreen: '#E8F5E3',
  white: '#FFFFFF',
  black: '#000000',
  gray: '#888',
  red: '#e74c3c',
};

export const Container = styled(View)`
  flex: 1;
  background-color: ${theme.white};
`;

export const Header = styled(View)`
  align-items: center;
  padding: 16px;
`;

export const HeartIcon = styled(Image)`
  width: 40px;
  height: 40px;
`;

export const Separator = styled(View)`
  width: 100%;
  height: 1px;
  background-color: ${theme.primaryGreen};
`;

export const Content = styled(View)`
    flex: 1;
    padding: 16px;
`;

export const Title = styled(Text)`
  font-size: 24px;
  font-weight: bold;
  color: ${theme.black};
  margin-bottom: 24px;
`;

export const ActionButton = styled(TouchableOpacity)`
    background-color: ${theme.lightGreen};
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 16px;
    flex-direction: row;
    align-items: center;
`;

export const ActionButtonText = styled(Text)`
    font-size: 18px;
    color: ${theme.black};
    margin-left: 16px;
`;

export const LogoutButton = styled(TouchableOpacity)`
    background-color: ${theme.red};
    padding: 16px;
    border-radius: 12px;
    margin: 16px;
    align-items: center;
`;

export const LogoutButtonText = styled(Text)`
    font-size: 18px;
    color: ${theme.white};
    font-weight: bold;
`;

export const ChangePasswordButton = styled(TouchableOpacity)`
    background-color: ${theme.primaryGreen};
    padding: 16px;
    border-radius: 12px;
    margin: 0 16px 16px 16px;
    align-items: center;
`;
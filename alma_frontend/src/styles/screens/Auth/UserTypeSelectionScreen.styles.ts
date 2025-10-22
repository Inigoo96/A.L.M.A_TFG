import {StyleSheet} from 'react-native';

export const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
  },

  // Header con logo
  header: {
    alignItems: 'flex-start',
    paddingTop: 16,
    paddingLeft: 16,
    paddingBottom: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#ACD467',
  },

  logo: {
    width: 40,
    height: 40,
  },

  helpButtonContainer: {
    position: 'absolute',
    top: 16,
    right: 16,
    zIndex: 10,
  },

  helpButton: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: '#FFFFFF',
    borderWidth: 2,
    borderColor: '#8CB875',
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },

  helpButtonText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#8CB875',
  },

  // Título central
  titleContainer: {
    alignItems: 'center',
    paddingVertical: 30,
  },

  mainTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#000000',
    textAlign: 'center',
  },

  // Container de tarjetas
  cardsContainer: {
    flex: 1,
    paddingHorizontal: 20,
    paddingTop: 10,
    paddingBottom: 20,
  },

  // Tarjeta individual
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 4},
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 5,
    overflow: 'hidden',
  },

  cardPressed: {
    transform: [{scale: 0.98}],
    opacity: 0.9,
  },

  cardImage: {
    width: '100%',
    height: 140,
    backgroundColor: '#F5F5F5',
  },

  cardContent: {
    padding: 20,
  },

  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },

  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#000000',
    flex: 1,
  },

  accessButton: {
    backgroundColor: '#D4E8CC',
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 20,
  },

  accessButtonText: {
    color: '#000000',
    fontSize: 14,
    fontWeight: '600',
  },

  cardDescription: {
    fontSize: 14,
    color: '#888888',
    lineHeight: 20,
  },

  // Footer
  footer: {
    paddingVertical: 20,
    paddingHorizontal: 30,
    alignItems: 'center',
  },

  footerText: {
    fontSize: 13,
    color: '#8CB875',
    textAlign: 'center',
    fontWeight: '500',
  },
});
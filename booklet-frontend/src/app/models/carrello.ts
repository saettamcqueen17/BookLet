export interface OggettoCarrelloDTO {
  isbn: string;
  prezzoUnitario: number;
  titolo : string ;
  quantita: number;
}

export interface CarrelloDTO {
  oggetti: OggettoCarrelloDTO[];
  numeroTotaleOggetti: number;
  totale: number;
}

package es.pryades.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaxRate
{
	private double rate;
	private String value;
}

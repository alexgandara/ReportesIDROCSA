package imprimePDF;

import java.io.File;

















import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.itextpdf.text.DocumentException;

//import wsHomologador.detalle;



public class readXML_nc {


	public static factura_cabecera Cabecera = new factura_cabecera();
	public static factura_detalle[] Detalle = new factura_detalle[200];
	public static factura_detalle_email[] Detalle_email = new factura_detalle_email[200];
	public static reglones[] misReglones = new reglones[10];
	public static palabras[] arregloPalabras = new palabras[200];
	public static int _lineas_de_la_factura=0;
	public static int _lineas_Descripcion=0;

	//public static void main(String args[]) throws IOException {
		public static void readXML_nc(String _file_name, String _correos, parametros misParametros) throws IOException {




		System.out.println("Reporte de Nota de Credito Ver 2.0......");

		//	String _file_xml="R:\\conector\\data\\20525378358\\03_xmls_con_firma\\20525378358-01-F001-0000001.xml";

		String _file= _file_name;
		String _correo_destino = "";
		if (!isNullOrEmpty(_correos)) {
			_correo_destino = _correos;
		} else {
			_correo_destino= "nada";

		}


		String _ruc = _file.substring(0,11);



//		String _file_xml = ".\\data\\"+_ruc+"\\03_xmls_con_firma\\"+_file+".xml";
//		String _file_respuesta = ".\\data\\"+_ruc+"\\04_respuestas\\"+"r-"+_file+".xml";
//		String _file_pdf =  ".\\data\\"+_ruc+"\\05_pdfs\\"+_file+".pdf";
//		String _file_pdf_Membrete =  ".\\data\\"+_ruc+"\\05_pdfs\\Para_Imprimir_"+_file+".pdf";
//		String _file_html = ".\\data\\"+_ruc+"\\10_formatos\\formato.htm";
//		String _file_zip_respuesta = ".\\data\\"+_ruc+"\\04_respuestas\\"+"R-"+_file+".zip";
//		String _file_jpg = ".\\data\\"+_ruc+"\\10_formatos\\"+ "CartaCompletaMembrete.jpg";
		

		
		String _file_xml = misParametros.get_ruta_xml_con_firma()+_file+".xml";;
		String _file_respuesta = misParametros.get_ruta_respuestas()+"r-"+_file+".xml";
		String _file_pdf = misParametros.get_ruta_pdfs()+_file+".pdf";
		String _file_html = "t:\\conector_qr\\data\\20212562697\\10_formatos\\formato.htm";
		String _file_zip_respuesta = misParametros.get_ruta_respuestas()+"R-"+_file+".xml";;
		String _file_jpg = misParametros.get_ruta_formatos();
		String _file_jpg_para_membrete = misParametros.get_ruta_formatos_membrete();
		String _file_pdf_para_imprimir = misParametros.get_ruta_pdfs()+"Para_Imprimir_"+_file+".pdf";




		Cabecera.set_mensaje_html(readFile(_file_html));

		File fXmlFile = new File(_file_xml);
		try {




			String raya="----------------------------------------------------------------";

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);


			doc.getDocumentElement().normalize();

			//		NodeList nList = doc.getElementsByTagName("Invoice");

			System.out.println("DATOS DEL DOCUMENTO");

			System.out.println(raya);

			Cabecera.set_descripcion_documento(doc.getDocumentElement().getNodeName());	
			System.out.println("Documento _ _ _ _ _ _ : " + Cabecera.get_descripcion_documento());

			int tam = _file.length();
			String _temp=_file.substring(15,tam);
			if  (Cabecera.get_descripcion_documento().equals("CreditNote")) {
				// cbc:ID	//para serie y folio
				NodeList nList_id = doc.getElementsByTagName("cbc:ID");
				Node nNode_id = nList_id.item(5);
				//System.out.println("" + nNode_id.getNodeName());
				//		_temp = nNode_id.getTextContent();

			} else {
				// cbc:ID	//para serie y folio
				//			NodeList nList_id = doc.getElementsByTagName("cbc:ID");
				//			Node nNode_id = nList_id.item(7);
				//			//System.out.println("" + nNode_id.getNodeName());
				//			_temp = nNode_id.getTextContent();
			}

			System.out.println(_temp);
			Cabecera.set_serie(_temp.substring(0,4));
			Cabecera.set_folio(_temp.substring(5,_temp.length()));

			System.out.println("Serie _ _ _ _ _ _ _ _ _ _ _ _ _: " + Cabecera.get_serie());
			System.out.println("Folio _ _ _ _ _ _ _ _ _ _ _ _ _: " + Cabecera.get_folio());


			// cbc:ReferenceID
			NodeList nList_ReferenceID = doc.getElementsByTagName("cbc:ReferenceID");
			Node nNode_ReferenceID = nList_ReferenceID.item(0);
			Cabecera.set_doc_relacionado(nNode_ReferenceID.getTextContent());
			System.out.println("Documento Relacionado _ _ _ _ _: " + Cabecera.get_doc_relacionado());



			// cbc:ResponseCode 
			NodeList nList_ResponseCode = doc.getElementsByTagName("cbc:ResponseCode");
			Node nNode_ResponseCode = nList_ResponseCode.item(0);
			Cabecera.set_tipo_doc_relacionado(nNode_ResponseCode.getTextContent());
			System.out.println("Tipo de Documento Relacionado _: " + Cabecera.get_tipo_doc_relacionado());

			// cbc:Description motivo de la anulacion
			NodeList nList_Description_null = doc.getElementsByTagName("cbc:Description");
			Node nNode_Description_null = nList_Description_null.item(0);
			Cabecera.set_motivo_de_anulacion(nNode_Description_null.getTextContent());
			System.out.println("Motivo de la Anulacion _ _ _ _ : " + Cabecera.get_motivo_de_anulacion());




			// cbc:IssueDate
			NodeList nList_IssueDate = doc.getElementsByTagName("cbc:IssueDate");
			Node nNode_IssueDate = nList_IssueDate.item(0);

			String _fecha = nNode_IssueDate.getTextContent();

			String _Dia = "";
			String _Mes = "";
			String _Ano = "";
			_Dia = _fecha.substring(8, 10);  //2016.09.17  2016-11-30
			_Mes = _fecha.substring(5, 7);  //2016.09.17  0123456789
			_Ano = _fecha.substring(0, 4);             // 1234567890
			Cabecera.set_fecha( _Dia+"/"+_Mes+"/"+_Ano);
			System.out.println("Fecha del Docto _ _ _ _ _ _ _ _: " + Cabecera.get_fecha());	




			if (Cabecera.get_descripcion_documento().equals("CreditNote")) {
				Cabecera.set_tipo_doc_descripcion("NOTA DE CREDITO");
			} else {
				// cbc:InvoiceTypeCode
				NodeList nList_InvoiceTypeCode = doc.getElementsByTagName("cbc:InvoiceTypeCode");
				Node nNode_InvoiceTypeCode = nList_InvoiceTypeCode.item(0);
				Cabecera.set_tipo_doc(nNode_InvoiceTypeCode.getTextContent());
				System.out.println("Tipo del Documento: _ _ _ _ _ _: " + Cabecera.get_tipo_doc());
				Cabecera.set_tipo_doc_descripcion("FACTURA");


				if (Cabecera.get_tipo_doc().substring(1).equals("3")) {
					Cabecera.set_tipo_doc_descripcion("BOLETA");
				}


				if (Cabecera.get_tipo_doc().substring(1).equals("7")) {
					Cabecera.set_tipo_doc_descripcion("NOTA DE CREDITO");
				}

				if (Cabecera.get_tipo_doc().substring(1).equals("8")) {
					Cabecera.set_tipo_doc_descripcion("NOTA DE DEBITO");
				}
			}



			// cbc:ResponseCode
			NodeList nList_InvoiceTypeCode = doc.getElementsByTagName("cbc:ReferenceID");
			Node nNode_InvoiceTypeCode = nList_InvoiceTypeCode.item(0);
			String _uno=nNode_InvoiceTypeCode.getTextContent();
			System.out.println("+++++++++++++++++++");

			if (_uno.substring(0, 1).equals("F")) {
				Cabecera.set_tipo_doc("01");
			}


			if (_uno.substring(0, 1).equals("B")) {
				Cabecera.set_tipo_doc("03");
			}


			System.out.println("xxxTipo del Documento: _ _ _ _ _ _: " + Cabecera.get_tipo_doc());





			// cbc:DocumentCurrencyCode
			NodeList nList_DocumentCurrencyCode = doc.getElementsByTagName("cbc:DocumentCurrencyCode");
			Node nNode_DocumentCurrencyCode = nList_DocumentCurrencyCode.item(0);
			Cabecera.set_moneda(nNode_DocumentCurrencyCode.getTextContent());
			System.out.println("Tipo de Moneda_ _ _ _ _ _ _ _ _: " + Cabecera.get_moneda());

			System.out.println(raya);

			// cbc:CustomerAssignedAccountID "RUC DEL EMISOR"
			NodeList nList_CustomerAssignedAccountID = doc.getElementsByTagName("cbc:CustomerAssignedAccountID");
			Node nNode_CustomerAssignedAccountID = nList_CustomerAssignedAccountID.item(0);
			//20568335369 
			//para el caso del facturado es fijo
			Cabecera.set_ruc_emisor(nNode_CustomerAssignedAccountID.getTextContent());


			System.out.println("RUC del Emisor_ _ _ _ _ _ _ _ _: " + Cabecera.get_ruc_emisor());


			// cac:PartyName
			NodeList nList_PartyName = doc.getElementsByTagName("cac:PartyName");
			Node nNode_PartyName = nList_PartyName.item(0);
			Cabecera.set_razon_social_emisor(nNode_PartyName.getTextContent());
			System.out.println("Razon Social del Emisor_ _ _ _ : " + Cabecera.get_razon_social_emisor());


			// cbc:StreetName
			NodeList nList_StreetName = doc.getElementsByTagName("cbc:StreetName");
			Node nNode_StreetName = nList_StreetName.item(0);
			Cabecera.set_direccion_emisor(nNode_StreetName.getTextContent());
			System.out.println("Direccion del Emisor_ _ _ _ _ _: " + Cabecera.get_direccion_emisor());


			// cbc:ID	ubigeo
			NodeList nList_ubigeo = doc.getElementsByTagName("cbc:ID");
			Node nNode_ubigeo = nList_ubigeo.item(8);
			Cabecera.set_ubigeo_emisor(nNode_ubigeo.getTextContent());
			System.out.println("Ubigeo del Emisor _ _ _ _ _ _ _: " + Cabecera.get_ubigeo_emisor());


			// cbc:IdentificationCode
			NodeList nList_IdentificationCode = doc.getElementsByTagName("cbc:IdentificationCode");
			Node nNode_IdentificationCode = nList_IdentificationCode.item(0);
			Cabecera.set_pais_emisor(nNode_IdentificationCode.getTextContent());
			System.out.println("Pais del Emisor_ _ _ _ _ _ _ _ : " + Cabecera.get_pais_emisor());


			System.out.println(raya);

			// cbc:CustomerAssignedAccountID "RUC DEL RECEPTOR"
			NodeList nList_CustomerAssignedAccountID_r = doc.getElementsByTagName("cbc:CustomerAssignedAccountID");
			Node nNode_CustomerAssignedAccountID_r = nList_CustomerAssignedAccountID_r.item(1);
			Cabecera.set_ruc_receptor(nNode_CustomerAssignedAccountID_r.getTextContent());
			System.out.println("RUC del Receptor_ _ _ _ _ _ _ _: " + Cabecera.get_ruc_receptor());


			// cac:PartyName
			NodeList nList_PartyName_r = doc.getElementsByTagName("cbc:RegistrationName");
			Node nNode_PartyName_r = nList_PartyName_r.item(1);
			Cabecera.set_razon_social_receptor(nNode_PartyName_r.getTextContent());
			System.out.println("Razon Social del Receptor_ _ _ : " + Cabecera.get_razon_social_receptor());




			// cbc:direccion

			// cbc:Value  direccion
			NodeList nList_Value = doc.getElementsByTagName("cbc:StreetName");
			Node nNode_Value = nList_Value.item(1);
			Cabecera.set_direccion_receptor(nNode_Value.getTextContent());
			System.out.println("Direccion del Receptor_ _ _ _ _: " + Cabecera.get_direccion_receptor());




	
			System.out.println(raya);




			NodeList nList_pre = doc.getElementsByTagName("cac:PrepaidPayment");
			String _id_pre = "";
			double _prepaidAmount = 0;
			String _doc_id = "";



			for (int temp = 0; temp < nList_pre.getLength(); temp++) {

				Node nNode_pre = nList_pre.item(temp);


				Element eElement_pre = (Element) nNode_pre;

				_id_pre=eElement_pre.getElementsByTagName("cbc:ID").item(0).getTextContent();
				_prepaidAmount=Double.parseDouble(eElement_pre.getElementsByTagName("cbc:PaidAmount").item(0).getTextContent());
				_doc_id=eElement_pre.getElementsByTagName("cbc:InstructionID").item(0).getTextContent();
				//		System.out.println("ID:"+_id+" "+"Payable:"+_PayableAmount);








			}



			System.out.println("*************************************");
			//		Cabecera.set_total_descuentos(0);
			NodeList nList_ids = doc.getElementsByTagName("sac:AdditionalMonetaryTotal");
			String _id = "";
			double _PayableAmount = 0;


			for (int temp = 0; temp < nList_ids.getLength(); temp++) {

				Node nNode_ids = nList_ids.item(temp);


				Element eElement_ids = (Element) nNode_ids;

				_id=eElement_ids.getElementsByTagName("cbc:ID").item(0).getTextContent();
				_PayableAmount=Double.parseDouble(eElement_ids.getElementsByTagName("cbc:PayableAmount").item(0).getTextContent());

				//		System.out.println("ID:"+_id+" "+"Payable:"+_PayableAmount);




				if (_id.equals("1001")) {
					//cbc:PayableAmount MONTO GRABADO
					Cabecera.set_total_gravado(_PayableAmount);
					//			System.out.println("Importe Grabado_ _ _ _ _ _ _ _: " + Cabecera.get_total_gravado());
				}	


				if (_id.equals("1002")) {
					//cbc:PayableAmount MONTO inafecto
					Cabecera.set_total_inafecto(_PayableAmount);
					//			System.out.println("Importe Inafecto _ _ _ _ _ _ _: " + Cabecera.get_total_inafecto());
				}	


				if (_id.equals("1003")) {
					//cbc:PayableAmount MONTO exonerado
					Cabecera.set_total_exonerado(_PayableAmount);
					//			System.out.println("Importe Exonerado_ _ _ _ _ _ _: " + Cabecera.get_total_exonerado());
				}	

				if (_id.equals("1004")) {
					//cbc:PayableAmount MONTO exonerado
					//			Cabecera.set_total_gratuitas(_PayableAmount);
					//			System.out.println("Transferencia Gratuita _ _ _ _: " + Cabecera.get_total_gratuitas());
				}	



				if (_id.equals("2005")) {
					//cbc:PayableAmount MONTO descuentos
					//				Cabecera.set_total_descuentos(_PayableAmount);
					//			System.out.println("Importe Descuentos _ _ _ _ _ _: " + Cabecera.get_total_descuentos());
				}	





			}

			Cabecera.set_subtotal(Cabecera.get_total_gravado()+Cabecera.get_total_exonerado()+Cabecera.get_total_inafecto());
			System.out.println("Importe Sub Total_ _ _ _ _ _ _: " + Cabecera.get_subtotal());



			//cbc:TaxAmount
			NodeList nList_igv = doc.getElementsByTagName("cbc:TaxAmount");
			Node nNode_igv = nList_igv.item(0);
			Cabecera.set_total_igv(Double.parseDouble(nNode_igv.getTextContent()));
			System.out.println("Importe IGV_ _ _  _ _ _ _ _ _ : " + Cabecera.get_total_igv());



			// cac:LegalMonetaryTotal

			NodeList nList_total = doc.getElementsByTagName("cac:LegalMonetaryTotal");

			double _PayableAmount_total = 0;


			//	for (int temp = 0; temp < nList_total.getLength(); temp++) {

			Node nNode_total = nList_total.item(0);


			Element eElement_total = (Element) nNode_total;

			//	_id=eElement_ids.getElementsByTagName("cbc:ID").item(0).getTextContent();
			_PayableAmount_total=Double.parseDouble(eElement_total.getElementsByTagName("cbc:PayableAmount").item(0).getTextContent());





			/////////


			//cbc:PayableAmount
			//	NodeList nList_total = doc.getElementsByTagName("cbc:PayableAmount");
			//	Node nNode_total = nList_total.item(4);
			//	Cabecera.set_total(Double.parseDouble(nNode_total.getTextContent()));

			Cabecera.set_total(_PayableAmount_total);
			System.out.println("Importe Total_ _  _ _ _ _ _ _ : " + Cabecera.get_total());

					Cabecera.set_total_letra(denomina.main(Cabecera.get_total()));
					System.out.println("Importe con Letra _ _ _ _ _ _ : " + Cabecera.get_total_letra());


			// cbc:Value  importe con letra
			//			NodeList nList_Value = doc.getElementsByTagName("cbc:Value");
			//			Node nNode_Value = nList_Value.item(0);
			//			Cabecera.set_total_letra(nNode_Value.getTextContent());
			//			System.out.println("Importe con Letra _ _ _ _ _ _ : " + Cabecera.get_total_letra());


			// DigestValue
			NodeList nList_DigestValue = doc.getElementsByTagName("DigestValue");
			Node nNode_DigestValue = nList_DigestValue.item(0);
			Cabecera.set_codigo_hash(nNode_DigestValue.getTextContent());
			System.out.println("Codigo Hash_ _ _ _ _ _ _ _ _ : " + Cabecera.get_codigo_hash());



			// documentos relacionados dinamicos

			



			NodeList nList_rel = doc.getElementsByTagName("sac:AdditionalProperty");
			String _ids = "";
			String _texto = "";
			String _clave = "";
			int _tam = 0;

			System.out.println(" ** "+nList_rel.getLength());
			for (int temp = 0; temp < nList_rel.getLength(); temp++) {


				Node nNode_rel = nList_rel.item(temp);


				Element eElement_rel = (Element) nNode_rel;

				_id=eElement_rel.getElementsByTagName("cbc:ID").item(0).getTextContent();
				_texto=eElement_rel.getElementsByTagName("cbc:Value").item(0).getTextContent();

		//		System.out.println("ID:"+_id);
		//		System.out.println("TEXTO:"+_texto);

				_tam = _id.length();

				if (_id.equals("1010")) {


					int _tam_folio = _texto.length();

					Cabecera.set_serie(_texto.substring(0,4));
					Cabecera.set_folio(_texto.substring(5,_tam_folio));

					System.out.println("Serie _ _ _ _ _ _ _ _ _ _ _ _ _: " + Cabecera.get_serie());
					System.out.println("Folio _ _ _ _ _ _ _ _ _ _ _ _ _: " + Cabecera.get_folio());


				}


				if (_id.equals("1011")) {


					Cabecera.set_fecha_doc_rel(_texto);

					System.out.println("Serie _ _ _ _ _ _ _ _ _ _ _ _ _: " + Cabecera.get_serie());
					System.out.println("Folio _ _ _ _ _ _ _ _ _ _ _ _ _: " + Cabecera.get_folio());


				}








			}




			// sac:SUNATTransaction
			// tipo de opecaion
			Cabecera.set_tipo_operacion("-");


			NodeList nList_tran = doc.getElementsByTagName("sac:SUNATTransaction");
			//			String _ids = "";
			//			String _texto = "";
			//			String _clave = "";
			//			int _tam = 0;

			for (int temp = 0; temp < nList_tran.getLength(); temp++) {


				Node nNode_tran = nList_tran.item(temp);


				Element eElement_tran = (Element) nNode_tran;

				_id=eElement_tran.getElementsByTagName("cbc:ID").item(0).getTextContent();
				//		_texto=eElement_tran.getElementsByTagName("cbc:Value").item(0).getTextContent();

				//	System.out.println("Sunat Transaction ID:"+_id);
				//		System.out.println("TEXTO:"+_texto);




				if (_id.equals("01")) {
					Cabecera.set_tipo_operacion("Venta Interna");
				}



				if (_id.equals("02")) {
					Cabecera.set_tipo_operacion("Expotación");
				}


				if (_id.equals("03")) {
					Cabecera.set_tipo_operacion("No Domicilado");
				}

				if (_id.equals("04")) {
					Cabecera.set_tipo_operacion("Anticipo");
				}

				if (_id.equals("05")) {
					Cabecera.set_tipo_operacion("Vta Itinerante");
				}

				if (_id.equals("06")) {
					Cabecera.set_tipo_operacion("Factura Guia");
				}


			}







			Cabecera.set_sello(Cabecera.get_ruc_emisor()+"|"+
					Cabecera.get_tipo_doc()+"|"+
					Cabecera.get_serie()+"|"+
					Cabecera.get_folio()+"|"+
					Cabecera.get_total_igv()+"|"+
					Cabecera.get_total()+"|"+
					Cabecera.get_fecha()+"|"+
					""+"|"+		
					""+"|"+
					Cabecera.get_codigo_hash()+
					Cabecera.get_signature()
					);



			System.out.println(raya);
			System.out.println("Detalle del Documento_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");


			// cbc:ID	cantidad
			NodeList nList_linea = doc.getElementsByTagName("cbc:CreditedQuantity");

			System.out.println("numero de lineas _: " + nList_linea.getLength());	
			int _total_linea=nList_linea.getLength();

			for (int _n = 0; _n < 200; _n++) {
				Detalle[_n] = new factura_detalle();
			}


			for (int _n = 0; _n <100; _n++) {
				Detalle_email[_n] = new factura_detalle_email();
			}



			if  (Cabecera.get_descripcion_documento().equals("CreditNote")) {
				// cbc:Description
				
				
				int _lineaArreglo=0;
				int _lineas_email=0;
				
				
				
				for (int _linea = 0; _linea < _total_linea; _linea++) {
					NodeList nList_Description = doc.getElementsByTagName("cbc:Description");
					Node nNode_Description = nList_Description.item(_linea+1);
					String _text = nNode_Description.getTextContent();
					
					
				
					NodeList nList_CreditedQuantity = doc.getElementsByTagName("cbc:CreditedQuantity");
					Node nNode_CreditedQuantity = nList_CreditedQuantity.item(_linea);	
					Detalle[_lineaArreglo].set_cantidad(Double.parseDouble(nNode_CreditedQuantity.getTextContent()));
					if (nNode_CreditedQuantity.hasAttributes()) {
					    NamedNodeMap attributes = nNode_CreditedQuantity.getAttributes();
					    Node nameAttribute = attributes.getNamedItem("unitCode");
					    if (nameAttribute != null) {
					 
					        Detalle[_lineaArreglo].set_unidad(nameAttribute.getTextContent());
					    }
					}
					
					
					NodeList nList_SellersItemIdentification = doc.getElementsByTagName("cac:SellersItemIdentification");
					Node nNode_SellersItemIdentification = nList_SellersItemIdentification.item(_linea);
					Node nNode_codigo = nNode_SellersItemIdentification.getFirstChild();
					Detalle[_lineaArreglo].set_codigo(nNode_codigo.getTextContent());
					

	
					
					
					
					
					NodeList nList_LineExtensionAmount = doc.getElementsByTagName("cbc:LineExtensionAmount");
					Node nNode_LineExtensionAmount = nList_LineExtensionAmount.item(_linea);
					Detalle[_lineaArreglo].set_subtotal((Double.parseDouble(nNode_LineExtensionAmount.getTextContent())));
					
					
					NodeList nList_PriceAmount = doc.getElementsByTagName("cac:Price");
					Node nNode_PriceAmount = nList_PriceAmount.item(_linea);
					Detalle[_lineaArreglo].set_precio_unitario((Double.parseDouble(nNode_PriceAmount.getTextContent())));
					Detalle[_lineaArreglo].set_subtotal_sin_igv(Detalle[_linea].get_precio_unitario()*Detalle[_linea].get_cantidad());

					
					
					
					NodeList nList_TaxableAmount = doc.getElementsByTagName("cbc:TaxAmount");
					Node nNode_TaxableAmount = nList_TaxableAmount.item(_linea+4);
						try {
							Detalle[_lineaArreglo].set_igv((Double.parseDouble(nNode_TaxableAmount.getTextContent())));
						
							} catch (Exception e) {
								Detalle[_lineaArreglo].set_igv(0);
							}


					
					Detalle_email[_lineas_email].set_codigo(Detalle[_lineaArreglo].get_codigo());
					Detalle_email[_lineas_email].set_precio_unitario(Detalle[_lineaArreglo].get_precio_unitario());
					if (Detalle[_lineaArreglo].get_cantidad()>0) {
					Detalle_email[_lineas_email].set_cantidad(Detalle[_lineaArreglo].get_cantidad());
					}
					
					if (Detalle[_lineaArreglo].get_subtotal()>0) {
						Detalle_email[_lineas_email].set_subtotal(Detalle[_lineaArreglo].get_subtotal());
					}
					
					if (Detalle[_lineaArreglo].get_igv()>0) {
						Detalle_email[_lineas_email].set_igv(Detalle[_lineaArreglo].get_igv());
					}
					
					
					
					
					Detalle_email[_lineas_email].set_descripcion(_text);
					_lineas_email++;
					
					System.out.println("Text : "+_text);
					
					
					
					if (_text.length()<52) { 
						Detalle[_lineaArreglo].set_descripcion(nNode_Description.getTextContent());
						_lineaArreglo++;
						_lineas_Descripcion=_linea+_lineaArreglo;
						
					}  else {
						
						_lineaArreglo=_lineaArreglo+_linea;
						int y=llenaPalabras(_text);
						String _reglon="";
						int _tam_palabra=0;
						int _tam_linea=0;
						
						
						
						for (int _palabras=0; _palabras<=y-1; _palabras++) {
							_tam_palabra=arregloPalabras[_palabras].get_palabra().length();
						//	System.out.println("Palabra: "+arregloPalabras[_palabras].get_palabra());
							
							if ((_tam_linea+_tam_palabra)<52) {
								if (_reglon.equals("") && arregloPalabras[_palabras].get_palabra().equals(" ")) {
									
								} else {
									_reglon=_reglon+arregloPalabras[_palabras].get_palabra();
									_tam_linea=_tam_linea+_tam_palabra;
									}
								} else {
								_reglon=_reglon+arregloPalabras[_palabras].get_palabra();
								Detalle[_lineaArreglo-_linea].set_descripcion(_reglon);
								if (Detalle[_lineaArreglo-_linea].get_cantidad()==0) {
									Detalle[_lineaArreglo-_linea].set_codigo(".");
								}
								
								
								_reglon="";
								_tam_linea=0;
								
								_lineaArreglo++;
								 
								
								}
							}
							System.out.println("Linea Num:"+_lineaArreglo+"  "+_reglon);
							Detalle[_lineaArreglo-_linea].set_descripcion(_reglon);
							System.out.println(_reglon);
							
							if (Detalle[_lineaArreglo-_linea].get_cantidad()==0) {
								Detalle[_lineaArreglo-_linea].set_codigo(".");
								}
							_lineaArreglo++;
							try {
								Detalle[_lineaArreglo-_linea].set_descripcion("");
								_lineaArreglo++;
								_lineas_Descripcion=_linea+_lineaArreglo;
								//_lineaArreglo++;
								
							} catch (Exception e) {
								_lineaArreglo--;
							//	e.printStackTrace();
							}
							_lineas_Descripcion=_linea+_lineaArreglo;
							//_lineaArreglo++;
						
						}
				
				
					

					// Node nNode_Description = nList_Description.item(_linea+1);
					// Detalle[_linea].set_descripcion(nNode_Description.getTextContent());
					}
			}





















			int _linea_imp2=0;
			for (int _linea_imp=0;_linea_imp<_total_linea;_linea_imp++) {
				_linea_imp2=_linea_imp+1;
				System.out.println("");
				System.out.println("Linea_ _ _ _ _ _ _ _ _ _ _: " + _linea_imp2);
				System.out.println("Codigo_ _ _ _ _ _ _ _ _ _ : " + Detalle[_linea_imp].get_codigo());
				System.out.println("Unidad de Medida_ _ _ _ _ : " + Detalle[_linea_imp].get_unidad());
				System.out.println("Descripcion _ _ _ _ _ _ _ : " + Detalle[_linea_imp].get_descripcion());
				System.out.println("Cantidad_ _ _ _ _ _ _ _ _ : " + Detalle[_linea_imp].get_cantidad());
				System.out.println("Precio Unitario _ _ _ _ _ : " + Detalle[_linea_imp].get_precio_unitario());
				System.out.println("IGV _ _ _ _ _ _ _ _ _ _ _ : " + Detalle[_linea_imp].get_igv());
				System.out.println("Monto con IGV _ _ _ _ _ _ : " + Detalle[_linea_imp].get_subtotal());
				System.out.println("Monto sin IGV _ _ _ _ _ _ : " + Detalle[_linea_imp].get_subtotal_sin_igv());
				_lineas_de_la_factura=_linea_imp2;




			}


			//Cabecera.set_subtotal(Cabecera.get_total()-Cabecera.get_total_igv());

//			printPDFA4_NC.imp_factura(_file_xml, Cabecera, Detalle, _lineas_de_la_factura,_file_pdf, _file_jpg);		

			printPDFA4_NC.imp_factura(_file_xml, Cabecera, Detalle, _lineas_Descripcion,_file_pdf, _file_jpg);		

			printPDFA4_NC.imp_factura(_file_xml, Cabecera, Detalle, _lineas_Descripcion,_file_pdf_para_imprimir, _file_jpg_para_membrete);	

	


		//	printPDFA4.imp_factura(_file_xml, Cabecera, Detalle, _lineas_Descripcion,_file_pdf_para_imprimir, _file_jpg_para_membrete);
	
			
			
			
			System.out.println("Reporte PDF Generado:"+_file_pdf);
			if (_correo_destino=="nada") {
				System.out.println("Correo Vacio, no se envio correo...");
			} else {
				//	System.out.println("Enviando Correo a "+_correo_destino);
				//	email.main(_correo_destino,_file_xml,_file_pdf,_file,"");
				//	System.out.println("Correo Enviado.");

				System.out.println("Enviando Correo a "+_correo_destino);
				//		email.main(_correo_destino,_file_xml,_file_pdf,_file_respuesta,_file,Cabecera, Detalle, _lineas_de_la_factura, _file_zip_respuesta);

				System.out.println("Correo Enviado.");


			}



			//factura.imp_factura(_file_xml, Cabecera, Detalle);

			//	SFSprintPDF.imp_factura(_file_xml, Cabecera, Detalle, _lineas_de_la_factura,_file_pdf);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean isNullOrEmpty(String a) {
		return a == null || a.isEmpty();
	} 


	public static String readFile(String filename) throws IOException
	{
		String content = null;
		File file = new File(filename); //for ex foo.txt
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader !=null){reader.close();}
		}
		return content;
	}


	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}


	public static int llenaPalabras(String _cadena) 
	{
		int _tam = _cadena.length();
		String _car="";
		String _palabra="";
		int _tam_palabra=0;
		int _num_palabras=0;
		int _ult_pos=0;



		for (int x=0; x<=_tam-1; x++ ) {
			_car=_cadena.substring(x,x+1);
			//System.out.println(_car+"  "+x);

			_tam_palabra++;

			if (_car.equals(" ")) {

				_palabra=_cadena.substring(_ult_pos, _ult_pos+_tam_palabra);
				_ult_pos=x+1;
				_tam_palabra=0;
				arregloPalabras[_num_palabras] = new palabras();
				arregloPalabras[_num_palabras].set_palabra(_palabra);
				//System.out.println("la palabra que subi es "+_palabra);
				_num_palabras++;


			}

		}

		_palabra=_cadena.substring(_ult_pos, _ult_pos+_tam_palabra);
		//_ult_pos=x+1;
		//_tam_palabra=0;
		arregloPalabras[_num_palabras] = new palabras();
		arregloPalabras[_num_palabras].set_palabra(_palabra);
		//System.out.println("la palabra que subi es "+_palabra);
		_num_palabras++;

		return _num_palabras;
	}

}

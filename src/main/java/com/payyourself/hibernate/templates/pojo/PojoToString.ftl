<#if pojo.needsToString()>    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [\n");
<#foreach property in pojo.getToStringPropertiesIterator()>      buffer.append("${property.getName()}").append("='").append(${pojo.getGetterSignature(property)}()).append("'\n");			
</#foreach>      buffer.append("]\n");
      
      return buffer.toString();
     }
</#if>